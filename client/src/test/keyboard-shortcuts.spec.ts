import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Login").click();
  await expect(page.getByRole("heading", { name: "Quiz Whiz" })).toBeVisible();
});

test("D shortcut navigates to dashboard", async ({ page }) => {
  await page.keyboard.press("d");
  await expect(page.getByRole("button", { name: "Daily Game" })).toBeVisible();
});

test("L shortcut navigates to leaderboard", async ({ page }) => {
  await page.keyboard.press("l");
  await expect(page.getByRole("button", { name: "Daily Leaderboard" })).toBeVisible();
});

test("A shortcut navigates to daily leaderboard tab", async ({ page }) => {
  await page.keyboard.press("l");
  await page.keyboard.press("a");
  await expect(page.getByRole("columnheader", { name: "Rank" })).toBeVisible();
});

test("S shortcut navigates to topic leaderboard tab", async ({ page }) => {
  await page.keyboard.press("l");
  await page.keyboard.press("s");
  await expect(page.getByRole("columnheader", { name: "Category" })).toBeVisible();
});

test("T shortcut shows alert if already played", async ({ page }) => {
  page.once('dialog', async dialog => {
    expect(dialog.message()).toContain("already played");
    await dialog.dismiss();
  });
  await page.keyboard.press("t");
});

test("E shortcut navigates to endless mode", async ({ page }) => {
  await page.keyboard.press("e");
  await expect(page.getByPlaceholder("Please enter a topic")).toBeVisible();
});

test("Q shortcut logs user out", async ({ page }) => {
  await page.keyboard.press("q");
  await expect(page.getByLabel("Login")).toBeVisible();
});
