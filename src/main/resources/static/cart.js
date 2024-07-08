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
    alert('Food added successfully')
}

export async function fetchCartFood(){
    const token = verifyToken();

    const response = await fetch(`/api/cart/${getUserId()}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    verifyResponse(response);

    const data = await response.json();
    displayCartFood(data);
}

async function fetchCartActivity(){

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
            amount.textContent =   `Amount: ${food.amount}`
            foodDiv.appendChild(amount);

            foodLink.appendChild(foodDiv);
            foodList.appendChild(foodLink);
        });
    } else {
        document.getElementById('cartFoodListError').textContent = 'There is no foods in cart';
    }
}
function displayCartActivity(activities){

}

function displayInfo(){

}