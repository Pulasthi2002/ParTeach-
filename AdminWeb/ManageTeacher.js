import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, onValue, remove } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyDM6dKxDY61g3KS1xozyA2Cx0HVpHc7ER4",
    authDomain: "par-teach-mobile-app.firebaseapp.com",
    databaseURL: "https://par-teach-mobile-app-default-rtdb.firebaseio.com",
    projectId: "par-teach-mobile-app",
    storageBucket: "par-teach-mobile-app.appspot.com",
    messagingSenderId: "464003368122",
    appId: "1:464003368122:web:dc41f3ecf9ed5387f9ae83"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const database = getDatabase();

const userListDiv = document.getElementById('userList');
const searchBar = document.getElementById('searchBar');

// Function to display users
function displayUsers(users) {
    userListDiv.innerHTML = ''; 

    users.forEach((user) => {
        const userData = user.val();
        const userElement = document.createElement('div');
        userElement.classList.add('user');

        userElement.innerHTML = `
            <p><strong>Name:</strong> ${userData.name}</p>
            <p><strong>Email:</strong> ${userData.email}</p>
            <p><strong>Grade:</strong> ${userData.grade}</p>
            <p><strong>Address:</strong> ${userData.address}</p>
            <p><strong>Contact:</strong> ${userData.contact}</p>
            <button class="edit-btn" data-email="${user.key}">Edit</button>
            <button class="delete-btn" data-email="${user.key}">Delete</button>
            <hr>
        `;
        userListDiv.appendChild(userElement);
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

// Read users from the database
const usersRef = ref(database, 'teachers');
onValue(usersRef, (snapshot) => {
    const users = [];
    snapshot.forEach((childSnapshot) => {
        users.push(childSnapshot);
    });
    displayUsers(users);
});

// Function to handle edit button click
function handleEdit(event) {
    const email = event.target.dataset.email;
    // Redirect to the edit profile page with the email as a parameter
    window.location.href = `EditTeacher.html?email=${email}`;
}

// Function to handle delete button click
function handleDelete(event) {
    const email = event.target.dataset.email;
    // Implement delete functionality here
    remove(ref(database, 'teachers/' + email));
    console.log('Delete user with email:', email);
}

// Function to filter users by email
function filterUsers() {
    const filter = searchBar.value.toLowerCase();
    const users = Array.from(document.querySelectorAll('.user'));

    users.forEach(user => {
        const email = user.querySelector('p:nth-child(2)').innerText.toLowerCase();
        if (email.includes(filter)) {
            user.style.display = '';
        } else {
            user.style.display = 'none';
        }
    });
}

document.querySelector('.menu-icon').addEventListener('click', function() {
    document.querySelector('.nav-links').classList.toggle('active');
});

// Add event listener to search bar
searchBar.addEventListener('input', filterUsers);
