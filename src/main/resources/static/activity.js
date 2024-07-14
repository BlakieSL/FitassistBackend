import { verifyToken, verifyResponse, getUserId } from './utils.js';
const activityId = window.location.pathname.split('/').pop();

export async function fetchActivities(){
    const token = verifyToken();

    const response = await fetch('/api/activities', {
       headers: {
           'Authorization': `Bearer ${token}`
       }
    });
    verifyResponse(response);

    const data = await response.json();
    displayActivities(data);
}

export async function fetchActivity(){
    const token = verifyToken();

    const response = await fetch(`/api/activities/${activityId}`, {
       headers: {
           'Authorization': `Bearer ${token}`
       }
    });
    verifyResponse(response);

    const data = await response.json();
    displayActivityDetails(data, false)
}

export async function addActivity(){
    const token = verifyToken();

    const name = document.getElementById('name').value;
    const met = document.getElementById('met').value;
    const categoryName = document.getElementById('categoryName').value
    const response = await fetch('/api/activities', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: name, met: parseInt(met), categoryName: categoryName})
    });
    verifyResponse(response);

    alert('Activity added successfully');
    window.location.href = '/activities';
}

export async function calculateCaloriesBurned(){
    const token = verifyToken();
    const time = document.getElementById('time').value;

    const response = await fetch(`/api/activities/${activityId}/calculate-calories`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({userId: parseInt(getUserId()), time: parseInt(time)})
    })
    verifyResponse(response);

    const data = await response.json();
    displayActivityDetails(data, true);
}

export async function populateActivityTypes(){
    const token = verifyToken();

    const response = await fetch('/api/activity-categories', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    verifyResponse(response);

    const data = await response.json();
    displayActivityCategories(data);
}

export async function populateSpecificActivities(activityTypeId) {
    const token = verifyToken();
    let data;
    if(activityTypeId) {
        const response = await fetch(`/api/activity-categories/${activityTypeId}/activities`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        verifyResponse(response);

         data = await response.json();
    }else{
        data = [];
    }
    displaySpecificActivities(data);
}

function displayActivities(activities){
    const activityList = document.getElementById('activityList');
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

            const met = document.createElement('p');
            met.textContent = `MET: ${activity.met}`;
            activityDiv.appendChild(met);

            activityLink.appendChild(activityDiv)
            activityList.appendChild(activityLink);
        });
    } else {
        const noResults = document.createElement('p');
        noResults.textContent = 'No results found';
        activityList.appendChild(noResults);
    }
}

function displayActivityDetails(activity,flag){
    const activityName = document.getElementById('activityName');
    const activityDetails = document.getElementById('activityDetails');

    activityDetails.innerHTML = '';
    activityName.textContent = activity.name;

    const activityCalories = document.createElement('p');
    if(flag === true) {
        activityCalories.textContent = activity.caloriesBurn
    } else{
        activityCalories.textContent = activity.met
    }
    activityDetails.appendChild(activityCalories);
}

function displayActivityCategories(categories){
    const activityTypeSelect = document.getElementById('activityType');
    activityTypeSelect.textContent = '';

    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = 'Select a type';
    activityTypeSelect.appendChild(defaultOption);

    categories.forEach(category => {

        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.name;
        activityTypeSelect.appendChild(option);
    });
}

function displaySpecificActivities(activities) {
    const specificActivitySelect = document.getElementById('specificActivity');
    specificActivitySelect.textContent = '';

    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = 'Select an activity';
    specificActivitySelect.appendChild(defaultOption);

    activities.forEach(activity => {
        const option = document.createElement('option');
        option.value = activity.id;
        option.textContent = activity.name;
        specificActivitySelect.appendChild(option);
    });
}


