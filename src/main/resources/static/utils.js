export function getToken() {
    return localStorage.getItem('jwt');
}

export function verifyToken() {
    const token = getToken();
    if (!token) {
        throw new Error('No token found');
    }
    return token;
}

export function getUserId() {
    const payload = jwt_decode(verifyToken());
    return payload.userId;
}
export function getUserRole() {
    const payload = jwt_decode(verifyToken());
    return  payload.authorities || [];
}
export function verifyResponse(response) {
    if (response.status === 401) {
        window.location.href = '/login';
    }
    if (!response.ok) {
        throw new Error('Network response was not okay');
    }
}
