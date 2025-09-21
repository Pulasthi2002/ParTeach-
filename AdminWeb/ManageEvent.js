import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, onValue, remove } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";

const firebaseConfig = {
    apiKey: "AIzaSyDM6dKxDY61g3KS1xozyA2Cx0HVpHc7ER4",
    authDomain: "par-teach-mobile-app.firebaseapp.com",
    databaseURL: "https://par-teach-mobile-app-default-rtdb.firebaseio.com",
    projectId: "par-teach-mobile-app",
    storageBucket: "par-teach-mobile-app.appspot.com",
    messagingSenderId: "464003368122",
    appId: "1:464003368122:web:dc41f3ecf9ed5387f9ae83"
};

const app = initializeApp(firebaseConfig);
const database = getDatabase();
const eventList = document.getElementById('eventList');

// Function to display events
function displayEvents(events) {
    eventList.innerHTML = '';

    events.forEach((event) => {
        const eventData = event.val();
        const eventItem = document.createElement('li');
        eventItem.innerHTML = `
            <strong>Name:</strong> ${eventData.name}<br><br>
            <strong>Message:</strong> ${eventData.msgContent}<br><br>
            ${eventData.imageURL ? `<img src="${eventData.imageURL}" alt="${eventData.name}" style="max-width: 100%; border-radius: 8px; margin-top: 10px;">` : ''}
            <br>
            <button class="edit-btn" data-key="${event.key}">Edit</button>
            <button class="delete-btn" data-key="${event.key}">Delete</button>
            <hr>
        `;
        eventList.appendChild(eventItem);
    });

    // Attach event listeners to edit and delete buttons
    const editButtons = document.querySelectorAll('.edit-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');

    editButtons.forEach((button) => {
        button.addEventListener('click', handleEdit);
    });

    deleteButtons.forEach((button) => {
        button.addEventListener('click', handleDelete);
    });
}

// Read events from the database
const eventsRef = ref(database, 'events');
onValue(eventsRef, (snapshot) => {
    const events = [];
    snapshot.forEach((childSnapshot) => {
        events.push(childSnapshot);
    });
    displayEvents(events);
});

// Function to handle edit button click
function handleEdit(event) {
    const key = event.target.dataset.key;
    // Redirect to the edit event page with the key as a parameter
    window.location.href = `EditEvent.html?key=${key}`;
}

// Function to handle delete button click
function handleDelete(event) {
    const key = event.target.dataset.key;
    // Implement delete functionality here
    remove(ref(database, 'events/' + key));
    console.log('Deleted event with key:', key);
}
