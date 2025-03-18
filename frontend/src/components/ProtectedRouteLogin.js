
import { Navigate } from 'react-router-dom';

const ProtectedRouteLogin = ({ children }) => {
    const role = localStorage.getItem("role");
    
    if (role) {
        return <Navigate to="/loggedin" replace />;
    }

    return children;
};

export default ProtectedRouteLogin;
