// server.js
// Main application logic for a simple Node.js Todo App using Express

const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(express.static('public'));

let todos = [];
let nextId = 1;

// ---- REST API ----
app.get('/api/todos', (req, res) => {
  res.json(todos);
});

app.post('/api/todos', (req, res) => {
  const { text } = req.body;
  if (!text || text.trim() === '') {
    return res.status(400).json({ error: 'Todo text is required.' });
  }
  const todo = { id: nextId++, text: text.trim(), done: false };
  todos.push(todo);
  res.status(201).json(todo);
});

app.patch('/api/todos/:id', (req, res) => {
  const id = Number(req.params.id);
  const todo = todos.find(t => t.id === id);
  if (!todo) return res.status(404).json({ error: 'Not found' });

  const { text, done } = req.body;
  if (text !== undefined) todo.text = text.trim();
  if (done !== undefined) todo.done = !!done;
  res.json(todo);
});

app.delete('/api/todos/:id', (req, res) => {
  const id = Number(req.params.id);
  todos = todos.filter(t => t.id !== id);
  res.status(204).end();
});

// ---- Serve index.html ----
app.get('/', (_req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(port, () => {
  console.log(`Todo app listening at http://localhost:${port}`);
});
