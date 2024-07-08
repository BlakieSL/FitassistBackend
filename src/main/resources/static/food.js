// food.js
import { verifyToken, verifyResponse } from './utils.js';

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

function displayFoods(foods) {
    const foodList = document.getElementById('foodList');
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
