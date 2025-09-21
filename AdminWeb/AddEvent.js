import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, push, set } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";
import { getStorage, ref as sRef, uploadBytes, getDownloadURL } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-storage.js";

// Initialize Firebase with your project config
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

window.register = async function(e) {
    e.preventDefault();
    var name = document.getElementById("name").value;
    var msgContent = document.getElementById("msgContent").value;
    var imageFile = document.getElementById("image").files[0];

    try {
        let imageURL = '';

        if (imageFile) {
            // Create a storage reference and upload the file
            const storageRef = sRef(storage, 'events/' + imageFile.name);
            await uploadBytes(storageRef, imageFile);

            // Get the file's URL
            imageURL = await getDownloadURL(storageRef);
        }

        // Generate a new child location using push and get its key
        const newEventRef = push(ref(database, 'events'));

        // Store additional information in the Realtime Database with the generated key
        await set(newEventRef, {
            name: name,
            msgContent: msgContent,
            imageURL: imageURL
        });

        alert("Event Added Successfully");

        // Clear the input values
        document.getElementById("name").value = "";
        document.getElementById("msgContent").value = "";
        document.getElementById("image").value = "";

    } catch (error) {
        alert("Error adding event: " + error.message);
    }
};