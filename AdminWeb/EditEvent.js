import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, set, onValue } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";
import { getStorage, ref as storageRef, uploadBytesResumable, getDownloadURL } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-storage.js";

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
const storage = getStorage();
const editEventForm = document.getElementById('editEventForm');
const eventNameInput = document.getElementById('eventName');
const eventMessageInput = document.getElementById('eventMessage');
const eventImageInput = document.getElementById('eventImage');
const previewImage = document.getElementById('previewImage');
const urlParams = new URLSearchParams(window.location.search);
const eventKey = urlParams.get('key');

// Reference to the event in the database
const eventRef = ref(database, 'events/' + eventKey);

// Read event details from the database
onValue(eventRef, (snapshot) => {
    const eventData = snapshot.val();
    if (eventData) {
        // Populate form fields with event data
        eventNameInput.value = eventData.name;
        eventMessageInput.value = eventData.msgContent;
        if (eventData.imageURL) {
            previewImage.src = eventData.imageURL;
            previewImage.style.display = 'block';
        }
    } else {
        alert('Event Not Found.');
    }
});

// Event listener for file input change to display image preview
eventImageInput.addEventListener('change', (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.src = e.target.result;
            previewImage.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
});

// Update event details
editEventForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const updatedEventData = {
        name: eventNameInput.value,
        msgContent: eventMessageInput.value
    };

    const file = eventImageInput.files[0];

    if (file) {
        const storageRefPath = storageRef(storage, 'events/' + eventKey + '/' + file.name);
        const uploadTask = uploadBytesResumable(storageRefPath, file);

        uploadTask.on('state_changed', 
            (snapshot) => {
                // Progress handling (optional)
            }, 
            (error) => {
                console.error('Error uploading file:', error);
                alert('Error uploading file.');
            }, 
            () => {
                getDownloadURL(uploadTask.snapshot.ref).then((downloadURL) => {
                    updatedEventData.imageURL = downloadURL;

                    // Update event data in the database with imageURL
                    set(eventRef, updatedEventData)
                        .then(() => {
                            alert('Event Details Updated Successfully.');
                            // Clear the form fields
                            editEventForm.reset();
                            previewImage.style.display = 'none';
                            // Redirect to the manage event page
                            window.location.href = 'ManageEvent.html';
                        })
                        .catch((error) => {
                            console.error('Error updating event details:', error);
                            alert('Error updating event details.');
                        });
                });
            }
        );
    } else {
        // Update event data in the database without imageURL
        set(eventRef, updatedEventData)
            .then(() => {
                alert('Event Details Updated Successfully.');
                // Clear the form fields
                editEventForm.reset();
                previewImage.style.display = 'none';
                // Redirect to the manage event page
                window.location.href = 'ManageEvent.html';
            })
            .catch((error) => {
                console.error('Error updating event details:', error);
                alert('Error updating event details.');
            });
    }
});
