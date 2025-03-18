
import './App.css';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from './layout/Navbar';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';  
import EditUser from './pages/EditUser';
import ViewUser from './pages/ViewUser';
import LoginUser from './pages/LoginUser';
import SignUp from './pages/SignUp';
import UserList from './pages/UserList';
import { Switch, Redirect } from 'react-router-dom';
import "./components/Sidebar.css";
import Home from './pages/Home';
import { ProSidebarProvider } from 'react-pro-sidebar';
import Apartment from './pages/Apartment';
import Auth from './components/Auth';
import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom';


export const isLoggedIn = () => !!localStorage.getItem('token');


export const getRole = () => localStorage.getItem('role');

export const fetchWithAuth = (url, options = {}) => {
  const token = localStorage.getItem('token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return fetch(url, {
    ...options,
    headers
  });
};
function ProtectedRoute({ element, requiredRole }) {
  const navigate = useNavigate();
  
  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }
    
    // Redirect if doesn't have required role
    if (requiredRole && getRole() !== requiredRole) {
      alert('You do not have permission to access this page');
      navigate('/home');
    }
  }, [navigate, requiredRole]);
  
  return isLoggedIn() ? element : null;
}


function App() {
  return (
    <ProSidebarProvider>
    <div className="App">
      <Router>
      <Navbar/>
      <Routes>
        <Route exact path='/login' element={<LoginUser/>}/>
        <Route exact path='/home' element={<Home/>}/>
        <Route exact path='/admin' element={<UserList/>}/>
        <Route exact path='/register' element={<SignUp/>}/>
        <Route exact path='/edituser/:id' element={<EditUser/>}/>
        <Route exact path='/viewuser/:id' element={<ViewUser/>}/>
        <Route exact path='/apartment' element={<Apartment/>}/>
      </Routes>
      </Router>
    </div>
    </ProSidebarProvider>
  );
}

export default App;
