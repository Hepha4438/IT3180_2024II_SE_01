
import './App.css';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from './layout/Navbar';
import Home from './pages/Home';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';  
import EditUser from './users/EditUser';
import ViewUser from './users/ViewUser';
import LoginUser from './users/LoginUser';
import SignUp from './users/SignUp';

function App() {
  return (
    <div className="App">
      <Router>
      <Navbar/>
      <Routes>
        <Route exact path='/admin' element={<Home/>}/>
        <Route exact path='/register' element={<SignUp/>}/>
        <Route exact path='/edituser/:id' element={<EditUser/>}/>
        <Route exact path='/viewuser/:id' element={<ViewUser/>}/>
        <Route exact path='/login' element={<LoginUser/>}/>
      </Routes>
      </Router>
    </div>
  );
}

export default App;
