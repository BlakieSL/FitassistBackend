// script.js
import { login, register, logout } from './auth.js';
import { fetchUser, updateUser, deleteUser } from './user.js';
import { fetchFoods, fetchFood, calculateMacros } from './food.js';
import { getToken } from './utils.js';
import { addToCart, fetchCartFood  } from './cart.js';

document.addEventListener('DOMContentLoaded', init);

async function init() {
    const registerForm = document.getElementById('registerForm');
    const loginForm = document.getElementById('loginForm');
    const foods = document.getElementById('foods');
    const userInfo = document.getElementById('userInfo');
    const updateModal = document.getElementById('updateModal');
    const updateForm = document.getElementById('updateForm');
    const closeModal = document.querySelector('.close');
    const foodDetails = document.getElementById('foodDetails');
    const foodDetailsButtons = document.getElementById('foodDetailsButtons');
    const cart = document.getElementById('cart');

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

    if (foods) {
        try {
            await fetchFoods();

        } catch (error) {
            document.getElementById('foodListError').textContent = 'Error occurred during fetching food list';
        }
    }

    if (userInfo) {
        try {
            await fetchUser();
        } catch (error) {
           document.getElementById('userInfoError').textContent = 'Error occurred during fetching user information';
        }
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

    if(foodDetails){
        try{
            await fetchFood();
        } catch(error){
            document.getElementById('foodDetailsError').textContent = 'Error occurred during fetching food';
        }
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
    if(cart){
        try{
            await fetchCartFood();
        } catch(error){
            document.getElementById('cartError').textContent = 'Error occurred during fetching cart';
        }
    }
}
