import { expect, test } from "@playwright/test";

test('test game and leaderboard buttons are visible upon sign in', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible();
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).fill('example@gmail.com');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('Example!');
    await page.getByRole('button', { name: 'Continue' }).click();
    await expect(page.getByRole('button', { name: 'Daily Game' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Endless mode' })).toBeVisible();
    await expect(page.getByRole('paragraph')).toContainText('Today\'s Daily Word:');
    await expect(page.getByRole('button', { name: 'Leaderboard' })).toBeVisible();
    await expect(page.getByText('LeaderboardQuiz Whiz')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Open user button' })).toBeVisible();
  });

  test('test daily game board shows score, time, question, and answers', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible();
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).fill('example@gmail.com');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('Example!');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('button', { name: 'Daily Game' }).click();
    await expect(page.getByText('Score:')).toBeVisible();
    await expect(page.getByText('/10')).toBeVisible();
    await expect(page.getByRole('button', { name: 'A:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'B:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'C:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'D:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Return Home' })).toBeVisible();
  });

  test('test endless mode allows for any topic and then you can see the question and answers or return home', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible();
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).fill('example@gmail.com');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('Example!');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('button', { name: 'Endless mode' }).click();
    await expect(page.getByRole('button', { name: 'End Game' })).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Please enter a topic' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Play Game' })).toBeVisible();
    await page.getByRole('textbox', { name: 'Please enter a topic' }).click();
    await page.getByRole('textbox', { name: 'Please enter a topic' }).fill('Brown University');
    await page.getByRole('button', { name: 'Play Game' }).click();
    await expect(page.getByText('Streak:')).toBeVisible();
    await expect(page.getByText('Current Question: Which')).toBeVisible();
    await expect(page.getByRole('button', { name: 'A:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'B:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'C:' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'D:' })).toBeVisible();
    await page.getByRole('button', { name: 'End Game' }).click();
  });

  test('test leaderboard allows for daily or topic leaderboard to be shown', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await expect(page.getByRole('button', { name: 'Sign in' })).toBeVisible();
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).click();
    await page.getByRole('textbox', { name: 'Email address or username' }).fill('example@gmail.com');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('Example!');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('button', { name: 'Leaderboard' }).click();
    await expect(page.getByRole('button', { name: 'Daily Leaderboard' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Topic Leaderboard' })).toBeVisible();
    await page.getByRole('button', { name: 'Daily Leaderboard' }).click();
    await expect(page.getByRole('heading', { name: 'leaderboard-header' })).toBeVisible();
    await expect(page.getByLabel('Rank', { exact: true })).toContainText('Rank');
    await expect(page.getByLabel('Username', { exact: true })).toContainText('Username');
    await expect(page.getByLabel('Score', { exact: true })).toContainText('Score');
    await page.getByRole('button', { name: 'Topic Leaderboard' }).click();
    await expect(page.getByRole('heading', { name: 'leaderboard-header' })).toBeVisible();
    await expect(page.getByLabel('Category', { exact: true })).toContainText('Category');
    await expect(page.getByLabel('Username', { exact: true })).toContainText('Username');
    await expect(page.getByLabel('Streak')).toContainText('Streak');
  });