
import { Navigate } from 'react-router-dom';

const ProtectedRouteLogin = ({ children }) => {
    const token = localStorage.getItem("token");

    if (token) {
        return <Navigate to="/loggedin" replace />;
    }

    return children;
};

export default ProtectedRouteLogin;
