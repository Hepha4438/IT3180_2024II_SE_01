import { Navigate } from 'react-router-dom';


const ProtectedRouteWithId = ({ children, requiredId }) => {
    const id = localStorage.getItem("id");
    const isLoggedIn = localStorage.getItem("token") !== null;
    
    if (!isLoggedIn) {
        return <Navigate to="/login" replace />;
    }
    
    if (requiredRole && role !== requiredRole) {
        alert('You do not have permission to access this page');
        return <Navigate to="/home" replace />;
    }
    
    return children;
};

export default ProtectedRouteWithRole;