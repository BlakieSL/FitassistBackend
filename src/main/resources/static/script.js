// script.js
import { login, register, logout } from './auth.js';
import { fetchUser, updateUser, deleteUser } from './user.js';
import {fetchFoods, fetchFood, calculateMacros, searchFoods, fetchSuggestions, addFood} from './food.js';
import { getToken, getUserRole } from './utils.js';
import { addToCart, addToDailyActivity, fetchCart  } from './cart.js';
import {calculateCaloriesBurned, fetchActivities, fetchActivity, addActivity, populateActivityTypes, populateSpecificActivities} from './activity.js';

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
    const cart = document.getElementById('cart');
    const activities = document.getElementById('activities');
    const activityDetails = document.getElementById('activityDetails');
    const searchForm = document.getElementById('searchForm');
    const addFoodForm = document.getElementById('addFoodForm');
    const addActivityForm = document.getElementById('addActivityForm');

    const calculatorModal = document.getElementById('calculatorModal');
    const openCalculatorModal = document.getElementById('openCalculatorModal');
    const closeCalculatorModal = document.querySelector('.close');
    const calculatorForm = document.getElementById('calculatorForm');
    const searchQuery = localStorage.getItem('searchQuery');
    let loggedIn = false;

    if (getToken()) {
        loggedIn = true;
    }

    if(loggedIn) {
        const userRoles = getUserRole();
        console.log(userRoles);
        if (userRoles.includes('ROLE_ADMIN')) {
            document.querySelectorAll('.admin-only').forEach(element => {
                element.style.display = 'block';
            });
        }
    }

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
                await login();
            } catch (error) {
                document.getElementById('loginError').textContent = 'Error occurred during login. Please try again later.';
            }
        });
    }

    if (foods) {
        try {
            if (searchQuery) {
                await searchFoods(searchQuery);
                localStorage.removeItem('searchQuery');
            }else {
                await fetchFoods();
            }``
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
            await fetchCart();
        } catch(error){
            document.getElementById('cartError').textContent = 'Error occurred during fetching cart';
        }
    }

    if(activities){
        try{
            await fetchActivities();
        } catch(error){
            document.getElementById('cartActivityListError').textContent = 'Error occurred during fetching activity list';
        }
    }

    if(activityDetails){
        try{
            await fetchActivity();
        } catch(error){
            document.getElementById('activityDetailsError').textContent = 'Error occurred during fetching activity';
        }
        const calculateCaloriesBurnedBtn = document.getElementById('calculateCaloriesBurned');
        const addToCartBtn = document.getElementById('addToDailyActivities');

        calculateCaloriesBurnedBtn.addEventListener('click', async function(event){
           event.preventDefault();
           try{
               await calculateCaloriesBurned();
           } catch(error){
               document.getElementById('buttonsActivityError').textContent = 'Error occurred during calculations. Please try again later';
           }
        });
        addToCartBtn.addEventListener('click', async function (event){
            event.preventDefault();
            try{
                await addToDailyActivity();
            } catch(error){
                document.getElementById('buttonsActivityError').textContent = 'Error occurred during adding activity to daily activities. Please try again later.'
            }
        });
    }

    if(searchForm){
        searchForm.addEventListener('submit', async function(event){
           event.preventDefault();
           const query = document.getElementById('searchInput').value;
           localStorage.setItem('searchQuery', query);
           window.location.href = '/index';
        });

        const searchInput = document.getElementById('searchInput');
        const suggestionsContainer = document.createElement('div');
        suggestionsContainer.className = 'suggestions-container';
        searchInput.parentNode.appendChild(suggestionsContainer);

        searchInput.addEventListener('input', async function() {
            const query = searchInput.value;
            if (query.length > 1) {
               try {
                   await fetchSuggestions(query, suggestionsContainer);
               } catch(error){
                   console.log(error);
               }
            } else {
                suggestionsContainer.innerHTML = '';
            }
        });
    }

    if(addFoodForm){
        addFoodForm.addEventListener('submit', async function(event){
           event.preventDefault();
           try{
               await addFood();
           } catch(error){
               document.getElementById('addFoodError').textContent = 'Error occurred during adding food. Please try again later.';
           }
        });
    }

    if(addActivityForm){
        addActivityForm.addEventListener('submit', async function(event){
            event.preventDefault();
            try{
                await addActivity();
            } catch(error){
                document.getElementById('addActivityError').textContent = 'Error occurred during adding activity. Please try again later';
            }
        })
    }

    openCalculatorModal.addEventListener('click', async function(event){
       event.preventDefault();
       try{
            calculatorModal.style.display = 'block';
            await populateActivityTypes();
       } catch(error){
           document.getElementById('calculatorError').textContent = 'Error occurred during fetching activity types for calculations';
       }
    });

    closeCalculatorModal.addEventListener('click', function(){
        calculatorModal.style.display = 'none';
    })

    window.addEventListener('click', function(event) {
        if (event.target === calculatorModal) {
            calculatorModal.style.display = 'none';
        }
    });

    document.getElementById('activityType').addEventListener('change', async function(event){
       event.preventDefault();
       try{
            await populateSpecificActivities(this.value);
       } catch(error){
           document.getElementById('calculatorError').textContent = 'Error occurred during fetching specific activities for calculations';
       }
    });

   calculatorForm.addEventListener('submit', function(event) {
       event.preventDefault();
        const activity = document.getElementById('specificActivity').value;
        if(activity){
            window.location.href = `/activity/${activity}`;
        } else{
            document.getElementById('calculatorError').textContent = 'Please select an activity'
        }
    });
}
