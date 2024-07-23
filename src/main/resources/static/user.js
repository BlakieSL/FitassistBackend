import { verifyToken, getUserId, verifyResponse } from './utils.js';
import {logout} from "./auth";

export async function fetchUser() {
    const token = verifyToken();

    const response = await fetch(`/api/users/${getUserId()}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    verifyResponse(response);

    const data = await response.json();
    displayUserInfo(data);
}

export async function updateUser(user) {
    const token = verifyToken();

    const response = await fetch(`/api/users/${getUserId()}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    });

    verifyResponse(response);

    alert('User updated successfully');
    await fetchUser();
}

export async function deleteUser() {
    const token = verifyToken();

    const response = await fetch(`/api/users/${getUserId()}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    verifyResponse(response);

    alert('User deleted successfully');
    logout();
}

function displayUserInfo(user) {
    const userInfo = document.getElementById('userInfo');
    userInfo.innerHTML = '';
    if (userInfo) {
        const userDiv = document.createElement('div');
        userDiv.className = 'user-item';

        const userName = document.createElement('p');
        userName.textContent = user.name;
        userDiv.appendChild(userName);

        const userSurname = document.createElement('p');
        userSurname.textContent = user.surname;
        userDiv.appendChild(userSurname);

        const userEmail = document.createElement('p');
        userEmail.textContent = user.email;
        userDiv.appendChild(userEmail);

        const userGender = document.createElement('p');
        userGender.textContent = user.gender;
        userDiv.appendChild(userGender);

        const userAge = document.createElement('p');
        userAge.textContent = user.age;
        userDiv.appendChild(userAge);

        const userHeight = document.createElement('p');
        userHeight.textContent = user.height;
        userDiv.appendChild(userHeight);

        const userWeight = document.createElement('p');
        userWeight.textContent = user.weight;
        userDiv.appendChild(userWeight);

        const userActivityLevel = document.createElement('p');
        userActivityLevel.textContent = user.activityLevel;
        userDiv.appendChild(userActivityLevel);

        const userGoal = document.createElement('p');
        userGoal.textContent = user.goal;
        userDiv.appendChild(userGoal);

        const userCalories = document.createElement('p');
        userCalories.textContent = user.calculatedCalories;
        userDiv.appendChild(userCalories);

        userInfo.appendChild(userDiv);
    } else {
        const noResults = document.createElement('p');
        noResults.textContent = 'No results found';
        userInfo.appendChild(noResults);
    }
}
