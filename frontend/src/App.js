
import './App.css';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from './layout/Navbar';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';  
import EditUser from './pages/EditUser';
import ViewUser from './pages/ViewUser';
import LoginUser from './pages/Function 1/LoginUser';
import SignUp from './pages/SignUp';
import UserList from './pages/UserList';
import LoggedIn from './pages/Function 1/LoggedIn';
import "./components/Sidebar.css";
import Home from './pages/Home';
import { ProSidebarProvider } from 'react-pro-sidebar';
import Apartment from './pages/Function 3/Apartment';
import ProtectedRouteLogin from './components/ProtectedRouteLogin';
import ProtectedRouteWithRole from './components/ProtectedRouteWithRole';
import Header from './components/Sidebar';
import ViewApartment from './pages/Function 3/ViewApartment';
import EditApartment from './pages/Function 3/EditApartment';


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

function App() {
  const role = getRole();
  const isAuthenticated = isLoggedIn();
  return (
    <ProSidebarProvider>
    <div className="App">
      <Router>
      <Navbar/>
      <Routes>
        <Route exact path='/login' element={<ProtectedRouteLogin>
          <LoginUser/>
        </ProtectedRouteLogin>}/>
        <Route exact path='/loggedin' element={<LoggedIn/>}/>
        <Route exact path='/home' element={<Home/>}/>
        <Route exact path='/userslist' element={<ProtectedRouteWithRole requiredRole="ADMIN">
          <UserList/>
        </ProtectedRouteWithRole>}/>
        <Route exact path='/register' element={<ProtectedRouteWithRole requiredRole="ADMIN">
          <SignUp/>
        </ProtectedRouteWithRole>}/>
        <Route exact path='/edituser/:id' element={<EditUser/>}/>
        <Route exact path='/viewuser/:id' element={<ViewUser/>}/>
        <Route exact path='/apartment' element={<Apartment/>}/>
        <Route exact path='/viewapartment/:apartmentId' element={<ViewApartment/>}/>
        <Route exact path='/editapartment/:apartmentId' element={<EditApartment/>}/>
      </Routes>
      </Router>
    </div>
    </ProSidebarProvider>
  );
}

export default App;
