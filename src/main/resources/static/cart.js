import { verifyToken, verifyResponse, getUserId} from './utils.js';
export async function addToCart(){
    const token = verifyToken();
    const amount = document.getElementById('amountGrams').value;
    const foodId = window.location.pathname.split('/').pop();

    const response = await fetch(`/api/cart/${getUserId()}/add`, {
       method: 'POST',
       headers: {
           'Authorization': `Bearer ${token}`,
           'Content-Type': 'application/json'
       },
        body: JSON.stringify({ id: foodId, amount: parseInt(amount)})
    });

    verifyResponse(response);
    alert('Food added successfully');
}

export async function addToDailyActivity(){
    const token = verifyToken();
    const time = document.getElementById('time').value;
    const activityId = window.location.pathname.split('/').pop();

    const response = await fetch(`/api/daily-activities/${getUserId()}/add`, {
       method: 'POST',
       headers: {
           'Authorization': `Bearer ${token}`,
           'Content-Type': 'application/json'
       },
       body: JSON.stringify({ id: activityId, time: parseInt(time)})
    });

    verifyResponse(response);
    alert('Activity added successfully');
}

export async function fetchCart(){
    const token = verifyToken();

    const response = await fetch(`/api/cart/${getUserId()}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    verifyResponse(response);

    const response1 = await fetch(`/api/daily-activities/${getUserId()}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    verifyResponse(response1);

    const response2 = await fetch(`/api/users/${getUserId()}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    verifyResponse(response2);

    const foods = await response.json();
    const activities = await response1.json();
    const user = await response2.json();

    displayCartFood(foods);
    displayDailyActivities(activities)
    displayTotalMacros(foods, activities, user) ;
}



function displayCartFood(foods) {
    const foodList = document.getElementById('cartFoodList');
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

            const amount = document.createElement('p');
            amount.textContent = `Amount: ${food.amount}`
            foodDiv.appendChild(amount);

            foodLink.appendChild(foodDiv);
            foodList.appendChild(foodLink);
        });
    } else {
        const noResults = document.createElement('p');
        noResults.textContent = 'No results found';
        foodList.appendChild(noResults);
    }
}

function displayDailyActivities(activities) {
    const activityList = document.getElementById('cartActivityList');
    activityList.innerHTML = '';
    if (activities.length > 0) {
        activities.forEach(activity => {
            const activityLink = document.createElement('a');
            activityLink.href = `/activity/${activity.id}`;
            activityLink.className = 'activity-link';

            const activityDiv = document.createElement('div');
            activityDiv.className = 'activity-item';

            const activityName = document.createElement('h2');
            activityName.textContent = activity.name;
            activityDiv.appendChild(activityName);

            const activityCalories = document.createElement('p');
            activityCalories.textContent = `Calories Burned: ${activity.caloriesBurn}`;
            activityDiv.appendChild(activityCalories);

            const activityTime = document.createElement('p');
            activityCalories.textContent = `Time: ${activity.time}`;
            activityDiv.appendChild(activityTime);

            activityLink.appendChild(activityDiv)
            activityList.appendChild(activityLink);
        });
    } else {
        const noResults = document.createElement('p');
        noResults.textContent = 'No results found';
        activityList.appendChild(noResults);
    }
}

function displayTotalMacros(foods, activities,user) {
    let totalCalories = 0;
    let totalProteins = 0;
    let totalFats = 0;
    let totalCarbs = 0;
    let totalCaloriesBurned = 0;

    foods.forEach(food => {
        totalCalories += food.calories;
        totalProteins += food.protein;
        totalFats += food.fat;
        totalCarbs += food.carbohydrates;
    });

    activities.forEach(activity => {
       totalCaloriesBurned += activity.caloriesBurn;
    });

    document.getElementById('totalCalories').textContent = `${totalCalories}/${user.calculatedCalories}`;
    document.getElementById('totalProteins').textContent = `${totalProteins}`;
    document.getElementById('totalFats').textContent = `${totalFats}`;
    document.getElementById('totalCarbs').textContent = `${totalCarbs}`;
    document.getElementById('totalCaloriesBurned').textContent = `${totalCaloriesBurned}`;
}
