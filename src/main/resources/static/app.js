const dropZone = document.getElementById("dropZone");
const fileInput = document.getElementById("fileInput");
const preview = document.getElementById("preview");

const wineForm = document.getElementById("wineForm");
const mealInput = document.getElementById("meal")

let selectedFile = null;

function showFile(file) {
    selectedFile = file;
    preview.src = URL.createObjectURL(file);
    preview.hidden = false;
}

fileInput.addEventListener("change", () => {
    showFile(fileInput.files(0));
});


dropZone.addEventListener("click", () => {
    fileInput.click();
});

dropZone.addEventListener("dragover", (event) => {
    event.preventDefault();
    dropZone.classList.add("dragover");
});

dropZone.addEventListener("dragleave", () => {
    dropZone.classList.remove("dragover");
});

dropZone.addEventListener("drop", (event) => {
    event.preventDefault();
    dropZone.classList.remove("dragover")

    const file = event.dataTransfer.files[0];
    if (file && file.type.startsWith("image/")) {
        showFile(file);
    }
});

//Formular
wineForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    if (!selectedFile) {
        alert("Vælg et billede af vinkortet først");
        return;
    }

    const wineRequest = {
        wines: [],
        mealPairing: mealInput.value
    }
})






