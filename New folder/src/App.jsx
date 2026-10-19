import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_BASE = 'http://localhost:8082/api';

function App() {
  const [token, setToken] = useState('');
  const [authMode, setAuthMode] = useState('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [authError, setAuthError] = useState('');
  const [employees, setEmployees] = useState([]);
  const [activeTab, setActiveTab] = useState('employees');
  const [selectedEmpPayroll, setSelectedEmpPayroll] = useState(null);
  const [pageError, setPageError] = useState('');

  const api = (authToken = token) => axios.create({
    baseURL: API_BASE,
    headers: authToken ? { Authorization: `Bearer ${authToken}` } : {}
  });

  useEffect(() => {
    if (token) fetchEmployees(token);
  }, [token]);

  const fetchEmployees = async (authToken = token) => {
    try {
      const res = await api(authToken).get('/employees');
      setEmployees(res.data);
      setPageError('');
    } catch (err) {
      setEmployees([]);
      setPageError(err.response?.status === 403
        ? 'Your account does not have permission to view the employee directory. Sign in with an HR or ADMIN account.'
        : 'Could not load employees. Check that the backend and database are running.');
    }
  };

  const submitAuth = async (event) => {
    event.preventDefault();
    setAuthError('');
    try {
      const endpoint = authMode === 'login' ? '/auth/login' : '/auth/register';
      const body = authMode === 'login'
        ? { userOfficialEmail: email, password }
        : { username, userOfficialEmail: email, password };
      const response = await axios.post(`${API_BASE}${endpoint}`, body);
      if (!response.data?.token) {
        setAuthError('The server did not return a token. Check the backend configuration.');
        return;
      }
      setToken(response.data.token);
      setPassword('');
      setAuthError('');
    } catch (err) {
      setAuthError(err.response?.data?.detail || err.response?.data?.message ||
        (err.response?.status === 401 ? 'Invalid email or password.' : 'Authentication failed. Check the API and submitted details.'));
    }
  };

  const logout = () => {
    setToken('');
    setEmployees([]);
    setSelectedEmpPayroll(null);
    setPageError('');
  };

  const markAttendance = async (id) => {
    try {
      await api().post(`/attendance/checkin/${id}`);
      alert('Attendance marked successfully.');
    } catch (err) {
      alert(err.response?.data?.detail || 'Failed to mark attendance. It may already be marked today.');
    }
  };

  const processPayroll = async (id) => {
    try {
      const res = await api().post(`/payroll/generate/${id}`);
      setSelectedEmpPayroll(res.data);
      alert('Salary slip generated successfully.');
    } catch (err) {
      alert(err.response?.status === 403
        ? 'You need RUN_PAYROLL permission (ADMIN role) to generate payroll.'
        : (err.response?.data?.detail || 'Failed to generate payroll.'));
    }
  };

  if (!token) {
    return (
      <main style={{ fontFamily: 'Arial', background: '#f4f7fe', minHeight: '100vh', display: 'grid', placeItems: 'center', padding: 20 }}>
        <form onSubmit={submitAuth} style={{ width: 'min(420px, 100%)', background: 'white', padding: 32, borderRadius: 16, boxShadow: '0 4px 18px rgba(0,0,0,.08)' }}>
          <h2 style={{ color: '#2b3674' }}>NexusHR</h2>
          <p>{authMode === 'login' ? 'Sign in to your account' : 'Create an employee account'}</p>
          {authMode === 'register' && <input required value={username} onChange={e => setUsername(e.target.value)} placeholder="Username" style={inputStyle} />}
          <input required type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="Official email" style={inputStyle} />
          <input required minLength={8} type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password (at least 8 characters)" style={inputStyle} />
          {authError && <p role="alert" style={{ color: '#b42318' }}>{authError}</p>}
          <button type="submit" style={primaryButton}>{authMode === 'login' ? 'Sign in' : 'Register'}</button>
          <p style={{ marginTop: 18 }}>
            {authMode === 'login' ? 'New employee?' : 'Already registered?'}{' '}
            <button type="button" onClick={() => { setAuthMode(authMode === 'login' ? 'register' : 'login'); setAuthError(''); }} style={{ border: 0, background: 'none', color: '#4318ff', cursor: 'pointer' }}>
              {authMode === 'login' ? 'Create account' : 'Sign in'}
            </button>
          </p>
          {authMode === 'register' && <small>Public registration creates an EMPLOYEE account. HR/Admin permissions must be assigned by an authorized administrator.</small>}
        </form>
      </main>
    );
  }

  return (
    <div style={{ fontFamily: 'Arial', backgroundColor: '#f4f7fe', minHeight: '100vh' }}>
      <nav style={{ backgroundColor: '#2b3674', color: 'white', padding: 20, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
        <h2 style={{ margin: 0 }}>NexusHR - Employee Dashboard</h2>
        <div style={{ display: 'flex', gap: 16, alignItems: 'center', flexWrap: 'wrap' }}>
          {['employees', 'attendance', 'payroll'].map(tab => (
            <button key={tab} onClick={() => { setActiveTab(tab); setSelectedEmpPayroll(null); }} style={{ background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontWeight: activeTab === tab ? 'bold' : 'normal' }}>{tab[0].toUpperCase() + tab.slice(1)}</button>
          ))}
          <button onClick={logout} style={{ background: 'white', color: '#2b3674', border: 0, padding: '8px 12px', borderRadius: 6, cursor: 'pointer' }}>Sign out</button>
        </div>
      </nav>
      <div style={{ padding: 32 }}>
        {pageError && <p role="alert" style={{ color: '#b42318', background: '#fff', padding: 14, borderRadius: 8 }}>{pageError}</p>}
        {activeTab === 'employees' && <section>
          <h3 style={{ color: '#1b2559' }}>Employee Directory</h3>
          <button onClick={() => fetchEmployees()} style={primaryButton}>Refresh</button>
          <div style={{ overflowX: 'auto', marginTop: 16 }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', background: 'white', borderRadius: 12 }}>
              <thead><tr style={{ background: '#f7f9fa', textAlign: 'left' }}>{['Name', 'Email', 'Department', 'Designation'].map(x => <th key={x} style={{ padding: 14 }}>{x}</th>)}</tr></thead>
              <tbody>{employees.length === 0 ? <tr><td colSpan="4" style={{ padding: 20 }}>No employees to display.</td></tr> : employees.map(emp => <tr key={emp.id} style={{ borderBottom: '1px solid #e9edf7' }}><td style={cellStyle}>{emp.firstName} {emp.lastName}</td><td style={cellStyle}>{emp.email}</td><td style={cellStyle}>{emp.department}</td><td style={cellStyle}>{emp.designation}</td></tr>)}</tbody>
            </table>
          </div>
        </section>}
        {activeTab === 'attendance' && <section>
          <h3 style={{ color: '#1b2559' }}>Mark Daily Attendance</h3>
          <div style={cardGrid}>{employees.map(emp => <div key={emp.id} style={employeeCard}><h4>{emp.firstName} {emp.lastName}</h4><button onClick={() => markAttendance(emp.id)} style={{ ...primaryButton, background: '#05a67d' }}>Check-In</button></div>)}</div>
        </section>}
        {activeTab === 'payroll' && <section>
          <h3 style={{ color: '#1b2559' }}>Payroll Processing</h3>
          <div style={cardGrid}>{employees.map(emp => <div key={emp.id} style={employeeCard}><span>{emp.firstName} {emp.lastName}</span><button onClick={() => processPayroll(emp.id)} style={primaryButton}>Generate Slip</button></div>)}</div>
          {selectedEmpPayroll && <div style={{ ...employeeCard, maxWidth: 500, marginTop: 24 }}>
            <h3 style={{ textAlign: 'center' }}>SALARY SLIP</h3>
            <p><strong>Payment date:</strong> {selectedEmpPayroll.paymentDate}</p>
            <p><strong>Payroll month:</strong> {selectedEmpPayroll.payrollMonth}</p>
            <p><strong>Status:</strong> {selectedEmpPayroll.status}</p>
            <p>Base salary: ₹{selectedEmpPayroll.baseSalary}</p>
            <p>Allowances: ₹{selectedEmpPayroll.allowances}</p>
            <p>Deductions: -₹{selectedEmpPayroll.deductions}</p>
            <h3>Net salary: ₹{selectedEmpPayroll.netSalary}</h3>
          </div>}
        </section>}
      </div>
    </div>
  );
}

const inputStyle = { display: 'block', width: '100%', boxSizing: 'border-box', padding: 12, margin: '12px 0', border: '1px solid #d0d5dd', borderRadius: 8 };
const primaryButton = { background: '#4318ff', color: 'white', border: 0, padding: '10px 16px', borderRadius: 8, cursor: 'pointer' };
const cellStyle = { padding: 14 };
const cardGrid = { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(230px, 1fr))', gap: 16 };
const employeeCard = { background: 'white', padding: 20, borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,.05)' };

export default App;
