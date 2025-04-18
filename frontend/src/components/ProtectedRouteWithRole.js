import { Navigate } from 'react-router-dom';


const ProtectedRouteWithRole = ({ children, requiredRole }) => {
    const role = localStorage.getItem("role");
    const isLoggedIn = localStorage.getItem("token") !== null;
    
    if (!isLoggedIn) {
        alert('You must be logged in to access this page');
        return <Navigate to="/login" replace />;
    }
    
    if (requiredRole && role !== requiredRole) {
        alert('You do not have permission to access this page');
        return <Navigate to="/home" replace />;
    }
    
    return children;
};

export default ProtectedRouteWithRole;