const cards = [
    { src: "/images/card1.jpg", link: "#" },
    { src: "/images/card2.jpg", link: "#" },
];

let currentCardIndex = 0;

const cardImage = document.getElementById('card');
const cardLink = document.getElementById('cardLink');

function changeCard() {
    currentCardIndex = (currentCardIndex + 1) % cards.length;
    cardImage.src = cards[currentCardIndex].src;
    cardLink.href = cards[currentCardIndex].link;
}

setInterval(changeCard, 5000);