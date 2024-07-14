// auth.js
export async function login() {
    const username = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    const data = await response.json();
    localStorage.setItem('jwt', data.token);
    window.location.href = '/index';
}

export async function register() {
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const gender = document.getElementById('gender').value;
    const age = document.getElementById('age').value;
    const height = document.getElementById('height').value;
    const weight = document.getElementById('weight').value;
    const activityLevel = document.getElementById('activityLevel').value;
    const goal = document.getElementById('goal').value;

    const response = await fetch('/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, surname, email, password, gender, age, height, weight, activityLevel, goal })
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    window.location.href = '/login';
}

export function logout() {
    localStorage.removeItem('jwt');
    window.location.href = '/login';
}
