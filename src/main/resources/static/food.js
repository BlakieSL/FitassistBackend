import {verifyResponse, verifyToken} from './utils.js';

const foodId = window.location.pathname.split('/').pop();

export async function fetchFoods() {
    const token = verifyToken();

    const response = await fetch('/api/foods', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    verifyResponse(response);

    const data = await response.json();
    displayFoods(data);
}

export async function fetchFood(){
    const token = verifyToken();

    const response = await fetch(`/api/food/${foodId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    verifyResponse(response);

    const data = await response.json();
    displayFoodDetails(data);
}

export async function calculateMacros(){
    const token = verifyToken();
    const amount = document.getElementById('amountGrams').value;

    const response = await fetch(`/api/food/${foodId}`, {
       method: 'POST',
       headers: {
           'Authorization': `Bearer ${token}`,
           'Content-Type': 'application/json'
       },
        body: JSON.stringify({amount: parseInt(amount)})
    });

    verifyResponse(response);

    const data = await response.json();
    displayFoodDetails(data);
}

export async function searchFoods(query){
    const token = verifyToken();

    const response = await fetch('/api/foods/search', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
       },
       body: JSON.stringify({name: query})
    });
    verifyResponse(response);

    const data = await response.json();
    displayFoods(data);
}

export async function fetchSuggestions(query, container) {
    const token = verifyToken();

    const response = await fetch('/api/foods/search', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name: query })
    });
    verifyResponse(response);

    const data = await response.json();
    displaySuggestions(data,container);
}


function displayFoods(foods) {
    const foodList = document.getElementById('foodList');
    foodList.innerHTML = '';
    if (foods.length > 0) {
        foods.forEach(food => {
            const foodLink = document.createElement('a');
            foodLink.href = `/food/${food.id}`;
            foodLink.className = 'food-link';

            const foodDiv = document.createElement('div');
            foodDiv.className = 'food-item';

            const foodName = document.createElement('h2');
            foodName.textContent = food.name;
            foodDiv.appendChild(foodName);

            const foodCalories = document.createElement('p');
            foodCalories.textContent = `Calories: ${food.calories}`;
            foodDiv.appendChild(foodCalories);

            foodLink.appendChild(foodDiv);
            foodList.appendChild(foodLink);
        });
    } else {
        const noResults = document.createElement('p');
        noResults.textContent = 'No results found';
        foodList.appendChild(noResults);
    }
}

function displayFoodDetails(food) {
    const foodName = document.getElementById('foodName');
    const foodDetails = document.getElementById('foodDetails');

    foodDetails.innerHTML = '';
    foodName.textContent = food.name;

    const foodCalories = document.createElement('p');
    foodCalories.textContent = `Calories: ${food.calories}`;
    foodDetails.appendChild(foodCalories);

    const foodProtein = document.createElement('p');
    foodProtein.textContent = `Proteins: ${food.protein}`;
    foodDetails.appendChild(foodProtein);

    const foodFats = document.createElement('p');
    foodFats.textContent = `Fats: ${food.fat}`;
    foodDetails.appendChild(foodFats);

    const foodCarbs = document.createElement('p');
    foodCarbs.textContent = `Carbs: ${food.carbohydrates}`;
    foodDetails.appendChild(foodCarbs);

    const foodCategory = document.createElement('p');
    foodCategory.textContent = `Carbs: ${food.categoryName}`;
    foodDetails.appendChild(foodCategory);
}

function displaySuggestions(suggestions, container) {
    container.innerHTML = '';
    if (suggestions.length > 0) {
        const ul = document.createElement('ul');
        ul.className = 'suggestions-list';
        suggestions.forEach(food => {
            const li = document.createElement('li');
            li.className = 'suggestion-item';
            li.textContent = food.name;
            li.addEventListener('click', () => {
                document.getElementById('searchInput').value = food.name;
                container.innerHTML = '';
            });
            ul.appendChild(li);
        });
        container.appendChild(ul);
    }
}
