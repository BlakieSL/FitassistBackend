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

    function getToken() {
        return localStorage.getItem('jwt');
    }

    function verifyToken() {
        const token = getToken();
        if (!token) {
            throw new Error('No token found');
        }
        return token;
    }

    function getUserId() {
        const token = getToken();
        if (!token) {
            throw new Error('No token found');
        }
        const payload = jwt_decode(token);
        return payload.userId;
    }

    function verifyResponse(response) {
        if (response.status === 401) {
            window.location.href = '/login';
        }
        if (!response.ok) {
            throw new Error('Network response was not okay');
        }
    }

    function displayFoods(foods) {
        foodList.innerHTML = '';
        if (foods.length > 0) {
            foods.forEach(food => {
                const foodDiv = document.createElement('div');
                foodDiv.className = 'food-item';

                const foodName = document.createElement('h2');
                foodName.textContent = food.name;
                foodDiv.appendChild(foodName);

                const foodCalories = document.createElement('p');
                foodCalories.textContent = `Calories: ${food.calories}`;
                foodDiv.appendChild(foodCalories);

                const foodProtein = document.createElement('p');
                foodProtein.textContent = `Proteins: ${food.protein}`;
                foodDiv.appendChild(foodProtein);

                const foodFats = document.createElement('p');
                foodFats.textContent = `Fats: ${food.fat}`;
                foodDiv.appendChild(foodFats);

                const foodCarbs = document.createElement('p');
                foodCarbs.textContent = `Carbs: ${food.carbohydrates}`;
                foodDiv.appendChild(foodCarbs);

                foodList.appendChild(foodDiv);
            });
        } else {
            const noResults = document.createElement('p');
            noResults.textContent = 'No results found';
            foodList.appendChild(noResults);
        }
    }

    function displayUserInfo(user) {
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

    function logout() {
        localStorage.removeItem('jwt');
        window.location.href = '/login';
    }

    async function login() {
        const username = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        const response = await fetch('/api/login', {
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

    async function register() {
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

        const response = await fetch('/api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, surname, email, password, gender, age, height, weight, activityLevel, goal })
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        window.location.href = '/login';
    }

    async function fetchFoods() {
        const token = verifyToken();

        const response = await fetch('/api/foods', {
            headers: {
                'Authorization': `Bearer ${token}` // Corrected the string interpolation
            }
        });

        verifyResponse(response);

        const data = await response.json();
        displayFoods(data);
    }

    async function fetchUser() {
        const token = verifyToken();

        const response = await fetch(`/api/users/${getUserId()}`, {
            headers: {
                'Authorization': `Bearer ${token}` // Corrected the string interpolation
            }
        });

        verifyResponse(response);

        const data = await response.json();
        displayUserInfo(data);
    }

    async function updateUser(user) {
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

    async function deleteUser() {
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
}
