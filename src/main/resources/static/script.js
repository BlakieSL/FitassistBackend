// script.js
import { login, register, logout } from './auth.js';
import { fetchUser, updateUser, deleteUser } from './user.js';
import { fetchFoods, fetchFood, calculateMacros } from './food.js';
import { getToken } from './utils.js';
import { addToCart } from './cart.js';

document.addEventListener('DOMContentLoaded', init);

async function init() {
    const registerForm = document.getElementById('registerForm');
    const loginForm = document.getElementById('loginForm');
    const foodList = document.getElementById('foodList');
    const userInfo = document.getElementById('userInfo');
    const buttons = document.getElementById('buttons');
    const updateModal = document.getElementById('updateModal');
    const updateForm = document.getElementById('updateForm');
    const closeModal = document.querySelector('.close');
    const food = document.getElementById('foodDetails');
    const foodDetailsButtons = document.getElementById('foodDetailsButtons');

    if (!getToken() && window.location.pathname !== '/login' && window.location.pathname !== '/register') {
        window.location.href = '/login';
        return;
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            try {
                await register();
            } catch (error) {
                document.getElementById('registerError').textContent = 'Error occurred during register. Please try again later.';
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            try {
                await login(event);
            } catch (error) {
                document.getElementById('loginError').textContent = 'Error occurred during login. Please try again later.';
            }
        });
    }

    if (foodList) {
        try {
            await fetchFoods();

        } catch (error) {
            foodList.innerHTML = '';
            const p = document.createElement('p');
            p.textContent = "Error fetching food list";
            foodList.appendChild(p);
        }
    }

    if (userInfo) {
        try {
            await fetchUser();
        } catch (error) {
            userInfo.innerHTML = '';
            const p = document.createElement('p');
            p.textContent = "Error fetching user info";
            userInfo.appendChild(p);
        }
    }

    if (buttons) {
        const updateUserBtn = document.getElementById('updateUserBtn');
        const deleteUserBtn = document.getElementById('deleteUserBtn');
        const logoutBtn = document.getElementById('logoutBtn');

        updateUserBtn.addEventListener('click', function(event) {
            event.preventDefault();
            updateModal.style.display = 'block';
        });

        closeModal.addEventListener('click', function() {
            updateModal.style.display = 'none';
        });

        window.addEventListener('click', function(event) {
            if (event.target === updateModal) {
                updateModal.style.display = 'none';
            }
        });

        updateForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            try {
                const height = document.getElementById('height').value;
                const weight = document.getElementById('weight').value;
                const activityLevel = document.getElementById('activityLevel').value;
                const goal = document.getElementById('goal').value;

                const user = {
                    height: height,
                    weight: weight,
                    activityLevel: activityLevel,
                    goal: goal
                };

                await updateUser(user);
                updateModal.style.display = 'none';
            } catch (error) {
                document.getElementById('buttonsError').textContent = 'Error occurred during update. Please try again later.';
            }
        });

        deleteUserBtn.addEventListener('click', async function(event) {
            event.preventDefault();
            try {
                await deleteUser();
            } catch (error) {
                document.getElementById('buttonsError').textContent = 'Error occurred during delete. Please try again later.';
            }
        });

        logoutBtn.addEventListener('click', logout);
    }

    if(food){
        try{
            await fetchFood();
        } catch(error){
            food.innerHTML = '';
            const p = document.createElement('p');
            p.textContent = "Error fetching food";
            food.appendChild(p);
        }
    }

    if(foodDetailsButtons){
        const calculateMacrosBtn = document.getElementById('calculateMacros');
        const addToCartBtn = document.getElementById('addToCart');

        calculateMacrosBtn.addEventListener('click', async function (event){
           event.preventDefault();
           try{
               await calculateMacros();
           } catch(error){
               document.getElementById('buttonsError').textContent = 'Error occurred during calculations. Please try again later.'
           }
        });
        addToCartBtn.addEventListener('click', async function (event){
           event.preventDefault();
           try{
               await addToCart();
           } catch(error){
               document.getElementById('buttonsError').textContent = 'Error occurred during adding food to cart. Please try again later.'
           }
        });
    }
}
