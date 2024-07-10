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

    const response = await fetch(`/api/activity/${activityId}`, {
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
    const calories = document.getElementById('caloriesPerMinute').value;

    const response = await fetch('/api/activities/add', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: name, caloriesPerMinute: parseInt(calories)})
    });
    verifyResponse(response);

    alert('Activity added successfully');
    window.location.href = '/activities';
}

export async function calculateCaloriesBurned(){
    const token = verifyToken();
    const time = document.getElementById('time').value;

    const response = await fetch(`/api/activity/${activityId}`, {
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
        document.getElementById('activityListError').textContent = 'There are no activities';
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