import { verifyToken, verifyResponse} from './utils.js';
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
    displayActivityDetails(data)
}

export async function calculateCaloriesBurned(){
    const token = verifyToken();
    const time = document.getElementById('time').value;

    const response = await fetch(`/api/activity/${activityId}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'ContentType': 'application/json'
        },
        body: JSON.stringify({ time: parseInt(time)})
    })
    verifyResponse(response);

    const data = await response.json();
    displayActivityDetails(data);
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

            const activityCalories = document.createElement('p');
            activityCalories.textContent = `Calories Burned: ${activity.caloriesPerMinute}`;
            activityDiv.appendChild(activityCalories);

            activityLink.appendChild(activityDiv)
            activityList.appendChild(activityLink);
        });
    } else {
        document.getElementById('activityListError').textContent = 'There are no activities';
    }
}

function displayActivityDetails(activity){
    const activityName = document.getElementById('activityName');
    const activityDetails = document.getElementById('activityDetails');

    activityDetails.innerHTML = '';
    activityName.textContent = activity.name;

    const activityCalories = document.createElement('p');
    activityCalories.textContent = activity.caloriesPerMinute
    activityDetails.appendChild(activityCalories);
}