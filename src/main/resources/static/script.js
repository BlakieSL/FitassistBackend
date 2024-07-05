import { fetchFoods, fetchUser, deleteUser } from './apis.js';
import { displayFoods, displayUserInfo, initUserModal } from './components.js';
import { getToken } from './utils.js';

document.addEventListener('DOMContentLoaded', async function () {
    if (!getToken() && window.location.pathname !== '/login' && window.location.pathname !== '/register') {
        window.location.href = '/login';
        return;
    }

    initEventHandlers();
    initUserModal();

    if (document.querySelector('#foodList')) {
        try {
            const foods = await fetchFoods();
            displayFoods(foods);
        } catch (error) {
            const foodList = document.querySelector('#foodList');
            foodList.innerHTML = '';
            const p = document.createElement('p');
            p.textContent = "Error fetching food list";
            foodList.appendChild(p);
        }
    }

    if (document.querySelector('#userInfo')) {
        try {
            const user = await fetchUser();
            displayUserInfo(user);
        } catch (error) {
            const userInfo = document.querySelector('#userInfo');
            userInfo.innerHTML = '';
            const p = document.createElement('p');
            p.textContent = "Error fetching user info";
            userInfo.appendChild(p);
        }
    }
});

function initEventHandlers() {
    const buttons = document.getElementById('buttons');
    if(buttons){
        const updateUserBtn = document.getElementById('updateUserBtn');
        const deleteUserBtn = document.getElementById('deleteUserBtn');
        const logoutBtn = document.getElementById('logoutBtn');

        updateUserBtn.addEventListener('click', function(event) {
            event.preventDefault();
            document.getElementById('updateModal').style.display = 'block';
        });

        deleteUserBtn.addEventListener('click', async function(event){
            event.preventDefault();
            try{
                await deleteUser();
            } catch(error){
                document.getElementById('buttonsError').textContent = 'Error occurred during delete. Please try again later.';
            }
        });

        logoutBtn.addEventListener('click', function() {
            localStorage.removeItem('jwt');
            window.location.href = '/login';
        });
    }
}
