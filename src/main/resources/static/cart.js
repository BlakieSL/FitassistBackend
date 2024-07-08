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