window.onload = function () {
    var dotsWrapperEl = document.querySelector('.dots-wrapper');
    var dotsFragment = document.createDocumentFragment();
    for (var i = 0; i < 200; i++) {
        var dotEl = document.createElement('div');
        dotEl.classList.add('dot');
        dotsFragment.appendChild(dotEl);
    }

    dotsWrapperEl.appendChild(dotsFragment);

    function play() {
        var dots = document.getElementsByClassName("dot");

        [].forEach.call(dots, function (el) {

            el.addEventListener("click", function (e) {
                anime({
                    targets: '.dots-wrapper .dot',
                    scale: [
                        {value: .1, easing: 'easeOutSine', duration: 500},
                        {value: 1, easing: 'easeInOutQuad', duration: 1200}
                    ],
                    delay: anime.stagger(200, {grid: [20, 10], from: 'center'})
                });
            });
        });
        dots[0].click();
    }

    var rulesInfo = '1. You can send messages as to all either to selected participant trough private channel. <br>2. Using of obscene language is prohibited otherwise you will be banned. <br>3. This is group chat and you are free to log out any moment.';
    var rules = document.getElementById("welcome-code-rules");
    var description = document.getElementById("welcome-code-rules-description");
    var github = document.querySelector(".welcome-code-title");

    function createButton(el) {
        function hover() {
            anime.remove([rules]);
            anime({
                targets: rules,
                translateX: 0,
                translateY: 0,
                scale: 1,
                rotate: '1turn',
                duration: 500,
                easing: 'easeInOutSine',
                complete: function () {
                    description.innerHTML = '';
                    description.style.padding = "0";
                    rules.style.fontSize = "20px";

                }
            });
        }

        function down() {
            anime.remove([rules]);
            anime({
                targets: rules,
                translateX: 100,
                translateY: -120,
                scale: 3,
                rotate: '1turn',
                duration: 500,
                easing: 'easeInOutSine',
                complete: function () {
                    description.innerHTML = rulesInfo;
                    description.style.padding = "5px";
                    rules.style.fontSize = "5px";
                }
            });
        }

        function gitover() {
            anime.remove([github]);
            anime({
                targets: github,
                translateX: -20,
                scale: 2,
                rotate: '1turn',
                easing: 'easeInOutSine',
                duration: 200,
            });
        }

        function gitleave() {
            anime.remove([github]);
            anime({
                targets: github,
                translateX: 0,
                scale: 1,
                rotate: '1turn',
                easing: 'easeInOutSine',
                duration: 200,
            });
        }

        rules.addEventListener('mouseenter', down, false);
        description.addEventListener('mouseleave', hover, false);
        github.addEventListener('mouseenter', gitover, false);
        github.addEventListener('mouseleave', gitleave, false);
    }

    createButton();
    play();
}