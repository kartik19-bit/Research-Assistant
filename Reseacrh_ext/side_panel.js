 document.addEventListener('DOMContentLoaded', function() {
    chrome.storage.local.get(['researchNotes'], function(result) {
        if (result.researchNotes) {
            const data = result.researchNotes;
            document.getElementById('notes').value = result.researchNotes;
        } 
    });
    document.getElementById('summarization').addEventListener('click', summarizeText);
    document.getElementById('suggestion').addEventListener('click', getSuggestions);
    document.getElementById('save-notes').addEventListener('click', saveNotes);
 });

 async function summarizeText() {
    try{
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        const [{result}] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () =>  window.getSelection().toString() },
        );
        if(!result){
           showResults("No text selected. Please select some text to summarize.");
              return;
        }
        const response = await fetch('http://localhost:8080/api/research/process', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: result , operation : "summarize" })

        });
        if(!response.ok){
            throw new Error('Network response was not ok');
        }

        const text = await response.text();
        showResults(text.replace(/\\n/g, '<br>'));
     }

        catch(error){
            showResults("Error occurred while summarizing text.");
            console.error('Error:', error);
        }

 }

 async function getSuggestions() {
    try{
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        const [{result}] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () =>  window.getSelection().toString() },
        );
        if(!result){
           showResults("No text selected. Please select some text to get suggestions.");
              return;
        }
        const response = await fetch('http://localhost:8080/api/research/process', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: result , operation : "suggest" })

        });
        if(!response.ok){
            throw new Error('Network response was not ok');
        }

        const text = await response.text();
        showResults(text.replace(/\\n/g, '<br>'));
     }

        catch(error){
            showResults("Error occurred while summarizing text.");
            console.error('Error:', error);
        }

 }

 async function saveNotes() {
     const notes = document.getElementById('notes').value;
     chrome.storage.local.set({ researchNotes: notes }, function() {
         alert('Notes saved');
     });
 }

 function showResults(content) {
    document.getElementById('results').innerHTML = `<div class="result-item">  <div class="result-content">${content}</div></div>`;
 }