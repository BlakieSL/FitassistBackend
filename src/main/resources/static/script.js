document.addEventListener('DOMContentLoaded', init);

async function init() {
    const loginForm = document.getElementById('loginForm');
    const foodList = document.getElementById('foodList');
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');

    if (loginForm) {
        loginForm.addEventListener('submit', async function(event) {
            event.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            await login(username, password);
        });
    }

    if (foodList) {
        await fetchFoods();

        if (searchForm) {
            searchForm.addEventListener('submit', async function(event) {
                event.preventDefault();
                const query = searchInput.value;
                await searchFoods(query);
            });
        }
    }

    // Function to get JWT token from local storage
    function getToken() {
        return localStorage.getItem('jwt');
    }

    // Function to fetch all foods
    async function fetchFoods() {
        try {
            const token = getToken();
            console.log('Fetched Token:', token); // Debugging line
            if (!token) {
                throw new Error('No token found');
            }

            const response = await fetch('/api/foods', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            console.log('Fetch foods response status:', response.status); // Debugging line
            if (!response.ok) {
                if (response.status === 401) {
                    window.location.href = '/login';
                }
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            displayFoods(data);
        } catch (error) {
            console.error('Error occurred while fetching food list', error); // Debugging line
            foodList.innerHTML = ''; // Clear previous content
            const p = document.createElement('p');
            p.textContent = "Error fetching food list";
            foodList.appendChild(p);
        }
    }

    // Function to display foods
    function displayFoods(foods) {
        foodList.innerHTML = ''; // Clear previous results
        if (foods.length > 0) {
            foods.forEach(food => {
                const foodDiv = document.createElement('div');
                foodDiv.className = 'food-item';

                const foodName = document.createElement('h2');
                foodName.textContent = food.name;
                foodDiv.appendChild(foodName);

                const foodId = document.createElement('p');
                foodId.textContent = `ID: ${food.id}`;
                foodDiv.appendChild(foodId);

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

    // Function to handle login
    async function login(username, password) {
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });
            const data = await response.json();
            console.log('Login response:', data); // Debugging line
            if (data.token) {
                localStorage.setItem('jwt', data.token);
                console.log('Login successful, redirecting...'); // Debugging line
                window.location.href = '/index'; // Redirect to the main page
            } else {
                document.getElementById('loginError').textContent = 'Login failed. Please check your credentials.';
            }
        } catch (error) {
            document.getElementById('loginError').textContent = 'Error occurred during login. Please try again later.';
            console.error('Error occurred during login', error); // Debugging line
        }
    }
}
