// Mirrors the OOP structure of the Java version: an abstract-style FoodItem
// base "class" with one subclass per meal category.

class FoodItem {
  constructor(name, calories) {
    if (new.target === FoodItem) {
      throw new Error("FoodItem is abstract — use a subclass");
    }
    this.id = crypto.randomUUID();
    this.name = name;
    this.calories = calories;
  }
  getCategory() {
    throw new Error("Subclasses must implement getCategory()");
  }
}
class Breakfast extends FoodItem { getCategory() { return "Breakfast"; } }
class Lunch extends FoodItem     { getCategory() { return "Lunch"; } }
class Dinner extends FoodItem    { getCategory() { return "Dinner"; } }
class Snack extends FoodItem     { getCategory() { return "Snack"; } }

const CATEGORY_CLASS = { Breakfast, Lunch, Dinner, Snack };
const CATEGORY_ORDER = ["Breakfast", "Lunch", "Dinner", "Snack"];

const STORAGE_KEY = "calorieTracker.items";
const GOAL_KEY = "calorieTracker.goal";

// ---------- State ----------
let foodList = loadItems();

// ---------- DOM refs ----------
const addForm = document.getElementById("addForm");
const foodNameInput = document.getElementById("foodName");
const foodCaloriesInput = document.getElementById("foodCalories");
const foodCategorySelect = document.getElementById("foodCategory");
const formError = document.getElementById("formError");
const dailyGoalInput = document.getElementById("dailyGoal");

const receiptItemsEl = document.getElementById("receiptItems");
const receiptEmptyEl = document.getElementById("receiptEmpty");
const receiptTotalEl = document.getElementById("receiptTotal");

const factsTotalCalories = document.getElementById("factsTotalCalories");
const factsGoalValue = document.getElementById("factsGoalValue");
const factsRows = document.getElementById("factsRows");
const factsNote = document.getElementById("factsNote");

// ---------- Init ----------
document.getElementById("todayDate").textContent = new Date().toLocaleDateString(undefined, {
  weekday: "long", year: "numeric", month: "long", day: "numeric"
});
const savedGoal = localStorage.getItem(GOAL_KEY);
if (savedGoal) dailyGoalInput.value = savedGoal;

render();

// ---------- Events ----------
addForm.addEventListener("submit", (e) => {
  e.preventDefault();
  formError.textContent = "";

  const name = foodNameInput.value.trim();
  const caloriesRaw = foodCaloriesInput.value.trim();
  const category = foodCategorySelect.value;

  if (!name) { formError.textContent = "Please enter a food name."; return; }
  if (!caloriesRaw) { formError.textContent = "Please enter calories."; return; }

  const calories = Number(caloriesRaw);
  if (!Number.isInteger(calories) || calories <= 0) {
    formError.textContent = "Calories must be a positive whole number.";
    return;
  }

  const ItemClass = CATEGORY_CLASS[category] || Breakfast;
  foodList.push(new ItemClass(name, calories));
  saveItems();
  render();

  addForm.reset();
  foodCategorySelect.value = category;
  foodNameInput.focus();
});

dailyGoalInput.addEventListener("input", () => {
  const val = dailyGoalInput.value.trim();
  if (val) localStorage.setItem(GOAL_KEY, val);
  else localStorage.removeItem(GOAL_KEY);
  render();
});

// ---------- Rendering ----------
function render() {
  renderReceipt();
  renderNutritionFacts();
}

function renderReceipt() {
  receiptItemsEl.innerHTML = "";
  receiptEmptyEl.style.display = foodList.length ? "none" : "block";

  let total = 0;
  for (const item of foodList) {
    total += item.calories;
    const li = document.createElement("li");
    li.innerHTML = `
      <span class="item-name">${escapeHtml(item.name)}</span>
      <span class="item-cat">${item.getCategory()}</span>
      <span class="item-kcal">${item.calories}</span>
      <button type="button" aria-label="Delete ${escapeHtml(item.name)}">✕</button>
    `;
    li.querySelector("button").addEventListener("click", () => {
      foodList = foodList.filter(f => f.id !== item.id);
      saveItems();
      render();
    });
    receiptItemsEl.appendChild(li);
  }
  receiptTotalEl.textContent = total;
}

function renderNutritionFacts() {
  const total = foodList.reduce((sum, f) => sum + f.calories, 0);
  factsTotalCalories.textContent = total;

  const goalRaw = dailyGoalInput.value.trim();
  const goal = goalRaw ? Number(goalRaw) : null;
  if (goal && goal > 0) {
    const pct = Math.round((total / goal) * 100);
    factsGoalValue.textContent = `${goal} kcal · ${pct}% of goal`;
    factsNote.textContent = total > goal
      ? `* ${total - goal} kcal over today's goal.`
      : `* ${goal - total} kcal remaining today.`;
    factsNote.classList.toggle("over", total > goal);
  } else {
    factsGoalValue.textContent = "— not set —";
    factsNote.textContent = "* Set a daily goal on the order pad to track progress.";
    factsNote.classList.remove("over");
  }

  factsRows.innerHTML = "";
  for (const cat of CATEGORY_ORDER) {
    const catTotal = foodList
      .filter(f => f.getCategory() === cat)
      .reduce((sum, f) => sum + f.calories, 0);
    const pctOfTotal = total > 0 ? Math.round((catTotal / total) * 100) : 0;

    const row = document.createElement("div");
    row.className = "facts-row";
    row.dataset.cat = cat;
    row.innerHTML = `
      <div class="facts-row-top">
        <span>${cat}</span>
        <span>${catTotal}</span>
        <span>${pctOfTotal}%</span>
      </div>
      <div class="facts-bar-track">
        <div class="facts-bar-fill" style="width:${pctOfTotal}%"></div>
      </div>
    `;
    factsRows.appendChild(row);
  }
}

// ---------- Persistence ----------
function saveItems() {
  const plain = foodList.map(f => ({ name: f.name, calories: f.calories, category: f.getCategory() }));
  localStorage.setItem(STORAGE_KEY, JSON.stringify(plain));
}

function loadItems() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return parsed.map(p => {
      const ItemClass = CATEGORY_CLASS[p.category] || Breakfast;
      return new ItemClass(p.name, p.calories);
    });
  } catch {
    return [];
  }
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}
