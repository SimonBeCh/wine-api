const dropZone = document.getElementById("dropZone");
const fileInput = document.getElementById("fileInput");
const preview = document.getElementById("preview");
const wineForm = document.getElementById("wineForm");
const mealInput = document.getElementById("meal");
const result = document.getElementById("result");
const resetButton = document.getElementById("resetButton");

let selectedFile = null;

function showFile(file) {
    selectedFile = file;
    preview.src = URL.createObjectURL(file);
    preview.hidden = false;
}

fileInput.addEventListener("change", () => {
    showFile(fileInput.files[0]);
});

function showResult(data) {
    let html = `<p class="reason">${data.reason}</p>`;

    for (const wine of data.recommendedWines) {
        html += `
    <div class="wine">
        <div class="wine-header">
            <h3>${wine.name}</h3>
            <span class="price">${wine.price ?? ""} kr.</span>
        </div>
        <p>${wine.producent ?? ""} · ${wine.region ?? ""} · ${wine.vintage ?? ""}</p>
        <p>${wine.grapes?.join(", ") ?? ""}</p>
        <p>${wine.tasteNotes ?? ""}</p>
    </div>
`;
    }
    result.innerHTML = html;
}

function resetForm() {
    wineForm.reset();
    selectedFile = null;
    preview.hidden = true;
    preview.removeAttribute("src");
    result.innerHTML = "";
    resetButton.hidden = true;

    window.scrollTo({top: 0, behavior: "smooth"});
    mealInput.focus();
}

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
    dropZone.classList.remove("dragover");

    const file = event.dataTransfer.files[0];
    if (file && file.type.startsWith("image/")) {
        showFile(file);
    }
});

wineForm.addEventListener("submit", async (event) => {

    event.preventDefault();

    if (!selectedFile) {
        alert("vælg et billede af vinkortet først");
        return;
    }

    const formData = new FormData();
    formData.append("image", selectedFile);
    formData.append("mealPairing", mealInput.value);

    const button = wineForm.querySelector("button");
    button.disabled = true;
    button.textContent = "Finder vin...";
    result.innerHTML = "";

    try {
        const response = await fetch("/wine/image/suggest", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error("Serveren svarede med status " + response.status);
        }

        const data = await response.json();
        showResult(data);
    } catch (error) {
        console.error(error);
        result.innerHTML = `<p class="error">Noget gik galt. Prøv et tydeligere billede, eller prøv igen.</p>`;
    } finally {
        button.disabled = false;
        button.textContent = "Find vin";
        resetButton.hidden = false;
    }
});

resetButton.addEventListener("click",resetForm);