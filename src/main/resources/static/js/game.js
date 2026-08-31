const MOVE_ICON = {
    ROCK: '✊',
    PAPER: '✋',
    SCISSORS: '✌️'
};

const RESULT_TEXT = {
    WIN: 'YOU WIN!',
    LOSE: 'CPU WINS!',
    DRAW: 'DRAW!'
};

const playerHand = document.getElementById('playerHand');
const computerHand = document.getElementById('computerHand');
const playerIcon = playerHand.querySelector('.hand-icon');
const computerIcon = computerHand.querySelector('.hand-icon');
const countdown = document.getElementById('countdown');
const resultBanner = document.getElementById('resultBanner');
const buttons = document.querySelectorAll('.choice-btn');

const playerScoreEl = document.getElementById('playerScore');
const computerScoreEl = document.getElementById('computerScore');
const drawsEl = document.getElementById('draws');

const historyBody = document.getElementById('historyBody');
const historyTable = document.getElementById('historyTable');
const noRoundsMsg = document.getElementById('noRoundsMsg');

let busy = false;

buttons.forEach(btn => {
    btn.addEventListener('click', () => {
        if (busy) return;
        const move = btn.dataset.move;
        playRound(move);
    });
});

async function playRound(playerMove) {
    busy = true;
    setButtonsDisabled(true);

    // Reset visuals
    resultBanner.textContent = '';
    resultBanner.className = 'result-banner';
    playerHand.classList.remove('winner', 'loser', 'reveal');
    computerHand.classList.remove('winner', 'loser', 'reveal');

    // Countdown shake: Rock! Paper! Scissors!
    await shakeCountdown();

    // Call backend
    let data;
    try {
        const res = await fetch(`/api/games/${window.GAME_ID}/play?move=${playerMove}`, {
            method: 'POST'
        });
        data = await res.json();
    } catch (e) {
        countdown.textContent = 'Error!';
        busy = false;
        setButtonsDisabled(false);
        return;
    }

    // Reveal moves
    countdown.textContent = 'SHOOT!';
    playerIcon.textContent = MOVE_ICON[data.playerMove];
    computerIcon.textContent = MOVE_ICON[data.computerMove];
    playerHand.classList.add('reveal');
    computerHand.classList.add('reveal');

    await sleep(600);

    // Show result
    countdown.textContent = '';
    resultBanner.textContent = RESULT_TEXT[data.result];
    resultBanner.classList.add(data.result.toLowerCase());

    if (data.result === 'WIN') {
        playerHand.classList.add('winner');
        computerHand.classList.add('loser');
    } else if (data.result === 'LOSE') {
        computerHand.classList.add('winner');
        playerHand.classList.add('loser');
    }

    // Update scores
    animateScore(playerScoreEl, data.playerScore);
    animateScore(computerScoreEl, data.computerScore);
    animateScore(drawsEl, data.draws);

    // Prepend to history table
    prependHistoryRow(data);

    await sleep(1500);
    busy = false;
    setButtonsDisabled(false);
}

async function shakeCountdown() {
    const steps = ['ROCK!', 'PAPER!', 'SCISSORS!'];
    for (const label of steps) {
        countdown.textContent = label;
        countdown.classList.remove('pop');
        void countdown.offsetWidth; // reflow to restart animation
        countdown.classList.add('pop');

        playerHand.classList.remove('shake');
        computerHand.classList.remove('shake');
        void playerHand.offsetWidth;
        playerHand.classList.add('shake');
        computerHand.classList.add('shake');

        await sleep(500);
    }
    playerHand.classList.remove('shake');
    computerHand.classList.remove('shake');
}

function animateScore(el, newVal) {
    const oldVal = parseInt(el.textContent, 10);
    if (oldVal === newVal) return;
    el.classList.remove('bump');
    void el.offsetWidth;
    el.classList.add('bump');
    el.textContent = newVal;
}

function prependHistoryRow(data) {
    if (noRoundsMsg) {
        noRoundsMsg.style.display = 'none';
    }
    if (!historyTable) {
        location.reload();
        return;
    }
    const rowCount = historyBody.querySelectorAll('tr').length + 1;
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td>${rowCount}</td>
        <td>${data.playerMove}</td>
        <td>${data.computerMove}</td>
        <td class="${data.result.toLowerCase()}">${data.result}</td>
    `;
    historyBody.appendChild(tr);
    tr.classList.add('new-row');
}

function setButtonsDisabled(disabled) {
    buttons.forEach(b => {
        b.disabled = disabled;
        b.classList.toggle('disabled', disabled);
    });
}

function sleep(ms) {
    return new Promise(r => setTimeout(r, ms));
}