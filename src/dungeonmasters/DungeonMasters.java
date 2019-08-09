package dungeonmasters;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

public class DungeonMasters extends PApplet {
    float playerX = 256;
    float playerY = 352;
    float enemySpeed = 1f;
    float ammoSpeed;
    float xPos,yPos;
    float bulletSpeed = 5;
    float spawnRate = 300;
    boolean left, right, up, down;
    boolean enemiesSpawning = true;
    boolean shooting = false;
    double damage;
    double movementSpeed;
    int attackSpeed;
    int animationFrame = 1;
    int bulletFrame = 1;
    int score = 0;
    int highScore = 0;
    int speed;
    String character = "";
    String ammo;
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    enum GameState {
        START, ROLE, RULES, OVER, RUNNING
    }
    static GameState currentState;
    PFont scoreFont;
    PFont menuFont;
    PFont gameOverFont;
    PFont exitFont;
    PFont rulesFont;
    PImage backgroundImg;
    PImage[] playerAnim = new PImage[4];
    PImage[] explosionAnimation = new PImage[6];
    PImage[] bulletAnimation = new PImage[8];
    PImage[] enemyBulletAnimations = new PImage[4];
    PImage[][] enemyAnimations = new PImage[3][4];
    PImage gameOverImg;
    PImage restartButton;
    PImage rulesButton;
    PImage startButton;
    PImage startMenuImg;
    PImage exitButton;
    PImage rulesMenu;

    public static void main(String[] args) {
        PApplet.main("dungeonmasters.DungeonMasters");
    }

    public void settings() {
        size(512, 704);
    }

    public void setup() {
        loadEnemyAnimations();
        loadExplosionAnimation();
        xPos = 0;
        yPos = 0;
        speed = 2;

        backgroundImg = loadImage("Images/DungeonBackground.png");
        startMenuImg = loadImage("Images/StoneStartMenu.png");
        rulesMenu = loadImage("Images/StoneRulesMenu.png");
        gameOverImg = loadImage("Images/GameOverImg.png");
        restartButton = loadImage("Images/StoneButton.png");
        rulesButton = loadImage("Images/StoneButton.png");
        startButton = loadImage("Images/StoneButton.png");
        exitButton = loadImage("Images/StoneButton.png");

        gameOverImg.resize(300, 0);
        exitButton.resize(100, 40);
        restartButton.resize(200, 50);
        rulesButton.resize(200, 50);
        startButton.resize(200, 50);

        currentState = GameState.START;
    }

    public void draw() {
        drawBackground();

        switch (currentState) {
            case START:
                drawGameStart();
                break;
            case ROLE:
                drawRole();
                break;
            case RULES:
                drawRules();
                break;
            case OVER:
                drawGameOver();
                up = false;
                down = false;
                right = false;
                left = false;
                break;

            case RUNNING:
                drawScore();
                noStroke();

                if(frameCount % 60 == 0){
                    score++;
                }

                if (frameCount % 5 == 0) {
                    animationFrame++;
                    bulletFrame++;
                    animationFrame = animationFrame % 4;
                    bulletFrame = bulletFrame % 8;
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy en = enemies.get(i);
                        if (en.isDead == true) {
                            en.explosionFrame++;
                            shake();
                            if (en.explosionFrame == 5) {
                                enemies.remove(i);
                                xPos = 0;
                                yPos = 0;
                            }
                        }
                    }
                }

                drawPlayer();
                increaseDifficulty();
                shoot();


                for (int i = 0; i < enemies.size(); i++) {
                    Enemy en = enemies.get(i);
                    en.move(playerX, playerY);
                    en.drawEnemy();
                    en.attack(playerX,playerY);
                    for (int j = 0; j < bullets.size(); j++) {
                        Bullet b = bullets.get(j);
                        if (abs(b.x - en.x) < 30 && abs(b.y - en.y) < 30 && !en.isDead) {
                            if(en.health<=0) {
                                en.isDead = true;
                                bullets.remove(j);
                                score += 1;
                            }else{
                                en.isDead = false;
                                bullets.remove(j);
                                en.health--;
                            }
                            break;
                        }
                    }
                    if (abs(playerX - en.x) < 15 && abs(playerY - en.y) < 15 && en.explosionFrame ==0) {
                        if (score > highScore) {
                            highScore = score;
                        }
                        currentState = GameState.OVER;
                    }
                }
                break;
        }
    }

    public void drawGameOver() {
        imageMode(CENTER);
        image(startMenuImg, width / 2, height / 2);
        image(restartButton, width / 2, height / 2 + 100);
        gameOverFont = createFont("ModeNine" , 25, true);
        fill(130,0, 0);
        textFont(gameOverFont);
        textAlign(CENTER);
        text("Game Over ", width / 2, height / 2 - 90);
        text("Score: " + score, width / 2, height / 2 - 40);
        text("High Score: " + highScore, width / 2, height / 2 + 10);
        text("Restart ", width / 2 + 8, height / 2 + 110);
    }

    public void drawGameStart(){
        imageMode(CENTER);
        image(startMenuImg, width/2, height/2);
        image(rulesButton, width/2, height/2 - 12);
        image(rulesButton, width/2, height/2 - 77);
        image(startButton, width/2, height/2 + 52);
        menuFont = createFont("ModeNine", 40, true);
        textFont(menuFont);
        fill(130,0,0);
        textAlign(CENTER);
        text("MENU" , width/2, height/2 - 115);
        text("ROLE", width/2, height/2 - 65);
        text("RULES", width/2, height/2);
        text("START", width/2, height/2 + 65);
    }

    public void drawRole(){
        imageMode(CENTER);
        image(rulesMenu, width/2, height/2);
        image(rulesButton, width/2, height/2 - 77);
        image(rulesButton, width/2, height/2 - 12);
        image(rulesButton, width/2, height/2 + 52);
        textFont(menuFont);
        fill(130, 0, 0);
        textAlign(CENTER);
        text("KNIGHT", width/2, height/2 - 65);
        text("MAGE", width/2, height/2);
        text("ASSASSIN", width/2, height/2 + 65);
    }

    public void drawRules(){
        imageMode(CENTER);
        image(rulesMenu, width/2, height/2);
        image(exitButton, width/2, height/2 + 130);
        rulesFont = createFont("ModeNine", 18, true);
        textFont(rulesFont);
        textAlign(CENTER);
        fill(130, 0, 0);
        text("W : move up" + '\n' + "A : move left" + '\n' + "S : move down" + '\n' + "D : move right" + '\n' + "Click to shoot" + '\n' + '\n' + "Defend the dungeon" + '\n' + "by shooting incoming" + '\n' + "enemies while trying" + '\n' + "to stay alive." , width/2, height/2 - 135);
        exitFont = createFont("ModeNine" , 30, true);
        textFont(exitFont);
        textAlign(CENTER);
        fill(130, 0, 0);
        text("EXIT", width/2, height/2 + 142);
    }

    public void drawScore() {
        scoreFont = createFont("Leelawadee UI Bold", 26, true);
        textFont(scoreFont);
        fill(255, 255, 255);
        textAlign(CENTER);
        text("Score: " + score, width - 90, 20);
        text("High Score: " + highScore, width - 90, 50);
    }

    public void drawBackground() {
        background(250);
        imageMode(CORNER);
        image(backgroundImg, 0, 0);
        image(backgroundImg, xPos, yPos);
    }

    public void drawPlayer() {
        if (up) {
            playerY -= movementSpeed;
        }
        if (left) {
            playerX -= movementSpeed;
        }
        if (right) {
            playerX += movementSpeed;
        }
        if (down) {
            playerY += movementSpeed;
        }
        playerX = constrain(playerX, 70, width - 70);
        playerY = constrain(playerY, 70, height - 70);
        imageMode(CENTER);
        image(playerAnim[animationFrame], playerX, playerY);
    }

    public void loadEnemyAnimations(){
        for (int j = 1; j <= 4; j++) {
            enemyAnimations[0][j - 1] = loadImage("Images/FireSprite-" + j + ".png");
            enemyAnimations[1][j - 1] = loadImage("Images/IceSprite-" + j + ".png");
            enemyAnimations[2][j - 1] = loadImage("Images/Skull-" + j + ".png");

            enemyAnimations[0][j - 1].resize(80, 0);
            enemyAnimations[1][j - 1].resize(80, 0);
            enemyAnimations[2][j - 1].resize(50, 0);
        }
        for(int i = 0; i<=3; i++){
            enemyBulletAnimations[i] = loadImage("Images/Fireball_" + i + ".png");
            enemyBulletAnimations[i].resize(50,0);
        }
    }

    public void loadCharacterStats(String character, String ammo, float damage, float ammoSpeed,float movementSpeed, int attackSpeed) {
        this.character = character;
        this.ammo = ammo;
        this.damage = damage;
        this.ammoSpeed = ammoSpeed;
        this.movementSpeed = movementSpeed;
        this.attackSpeed = attackSpeed;
    }

    public void loadCharacter(){
        for (int i = 1; i <= 4; i++) {
            playerAnim[i - 1] = loadImage("Images/" + character + "-" + i + ".png");
            playerAnim[i - 1].resize(80, 0);
        }
        for(int a = 0; a <= 7; a++){
            bulletAnimation[a] = loadImage("Images/" + ammo + "_" + a + ".png");
            if (ammo.equals("Orb")){
                bulletAnimation[a].resize(50,0);
            }
        }
    }

    public void loadExplosionAnimation(){
        for (int i = 1; i <= 6; i++) {
            explosionAnimation[i - 1] = loadImage("Images/Explosion_FX" + i + ".png");
            explosionAnimation[i - 1].resize(60, 0);
        }
    }

    public void increaseDifficulty() {
        if (frameCount % spawnRate == 0) {
            generateEnemy();

            if (enemySpeed < 6) {
                enemySpeed += 0.1f;
            }
            if (spawnRate > 70) {
                spawnRate -= 10;
            }
        }
    }

    public void generateEnemy() {
        int side = (int) random(0, 2);
        int side2 = (int) random(0, 2);
        if (side % 2 == 0) { // top and bottom
            enemies.add(new Enemy(random(0, width), height * (side2 % 2), (int) random(0, 3)));
        } else { // sides
            enemies.add(new Enemy(width * (side2 % 2), random(0, height), (int) random(0, 3)));
        }
    }

    public void mousePressed() {
        switch (currentState) {
            case START:
                if(mouseX < 356 && mouseX > 156 && mouseY < 365 && mouseY > 315){
                    /**
                     * Print the Rules
                     */
                    currentState = GameState.RULES;
                }
                else if(mouseX < 356 && mouseX > 156 && mouseY < 439 && mouseY > 389){
                    if(character.isEmpty()) {
                        /**
                         * Automatically goes to Role page if
                         * you didn't pick a character
                         */
                        currentState = GameState.ROLE;
                        break;
                    }
                    else{
                        /**
                         * Game Running
                         */
                        currentState = GameState.RUNNING;
                    }
                }
                else if(mouseX < 356 && mouseX > 156 && mouseY > height/2 - 85 && mouseY < height/2 - 45){
                    /**
                     * Goes to role
                     */
                    currentState = GameState.ROLE;
                }
                break;

            case ROLE:
                if(mouseX < 356 && mouseX > 156 && mouseY > height/2 - 85 && mouseY < height/2 - 45){
                    loadCharacterStats(
                            "Knight",
                            "Axe",
                            2.0f,
                            -1.0f,
                            3.2f,
                            10
                    );
                    currentState = GameState.RUNNING;
                }
                else if(mouseX < 356 && mouseX > 156 && mouseY < 365 && mouseY > 315){
                    loadCharacterStats(
                            "Mage",
                            "Orb",
                            1.0f,
                            1.0f,
                            4.5f,
                            4
                    );
                    currentState = GameState.RUNNING;
                }
                else if(mouseX < 356 && mouseX > 156 && mouseY < 439 && mouseY > 389){
                    loadCharacterStats(
                            "Assassin",
                            "NinjaStar",
                            0.8f,
                            4.0f,
                            5.3f,
                            -1
                    );
                    currentState = GameState.RUNNING;
                }
                loadCharacter();
                break;

            case RULES:
                if(mouseX > width/2 - 50 && mouseX < width/2 + 50 && mouseY > height/2 + 110 && mouseY < height/2 + 150){
                    currentState = GameState.START;
                }
                break;

            case RUNNING:
                shooting = true;
                break;

            case OVER:
                if (mouseX > (width / 2 - 120) && mouseX < (width / 2 + 120) && mouseY > height / 2 + 100 - 25 && mouseY < (height / 2 + 100 + 25)) {
                    enemies.removeAll(enemies);
                    bullets.removeAll(bullets);
                    playerX = width/2;
                    playerY = height/2;
                    score = 0;
                    enemySpeed = 1f;
                    spawnRate = 300;
                    System.out.println(enemies.size());
                    currentState = GameState.START;
                }
                break;
        }
    }

    public void mouseReleased(){
        switch(currentState){
            case RUNNING:
                shooting = false;
                break;
        }
    }

    public void keyPressed() {
        switch(currentState) {
            case RUNNING:
                if (key == 'w') {
                    up = true;
                }
                if (key == 'a') {
                    left = true;
                }
                if (key == 's') {
                    down = true;
                }
                if (key == 'd') {
                    right = true;
                }
                break;
        }
    }

    public void keyReleased() {
        switch (currentState) {
            case RUNNING:
                if (key == 'w') {
                    up = false;
                }
                if (key == 'a') {
                    left = false;
                }
                if (key == 's') {
                    down = false;
                }
                if (key == 'd') {
                    right = false;
                }
                break;
        }
    }

    public void shoot(){
        float dx = mouseX - playerX;
        float dy = mouseY - playerY;
        float angle = atan2(dy, dx);
        float vx = (bulletSpeed + ammoSpeed) * cos(angle);
        float vy = (bulletSpeed + ammoSpeed) * sin(angle);
        if(frameCount % (10 + attackSpeed) == 0 && shooting == true){
            bullets.add(new Bullet(playerX, playerY, vx, vy));
        }
        for (int b = 0; b < bullets.size(); b++){
            Bullet bull = bullets.get(b);
            bull.move();
            bull.drawBullet();
            if(bull.x < 0 || bull.x > width || bull.y < 0 || bull.y > height){
                bullets.remove(b);
            }
        }
    }

    public void shake(){
        xPos = xPos + random(-5, 5)*speed;
        yPos = yPos + random(-5, 5)*speed;
    }

    class Enemy {
        float x, y, vx, vy;
        int enemyType = 0;
        boolean isDead = false;
        int explosionFrame = 0;
        double health = 1.0;
        ArrayList<EnemyBullet> enemyBullets = new ArrayList<EnemyBullet>();

        Enemy(float x, float y, int enemyType) {
            this.x = x;
            this.y = y;
            this.enemyType = enemyType;
            if(enemyType == 0){
                health = 2.0;
            }
            else if(enemyType == 1){
                health = 4.0;
            }
            else{
                health = 1.0;
            }
        }

        public void drawEnemy() {
            if (isDead == false) {
                imageMode(CENTER);
                image(enemyAnimations[enemyType][animationFrame], x, y);
            } else {
                image(explosionAnimation[explosionFrame], x, y);
            }
        }

        public void move(float px, float py) {
            if (isDead == false) {
                float angle = atan2(py - y, px - x);
                vx = cos(angle);
                vy = sin(angle);
                if(enemyType == 0){
                    x += vx * enemySpeed;
                    y += vy * enemySpeed;
                }
                else if(enemyType == 1){
                    x += vx;
                    y += vy;
                }
                else if(enemyType == 2){
                    x += vx * enemySpeed * 1.15;
                    y += vy * enemySpeed * 1.15;
                }
            }
        }
        public void attack(float px, float py) {
            if (enemyType == 0) {
                float angle = atan2(py - y, px - x);
                vx = cos(angle);
                vy = sin(angle);
                if (frameCount % 120 == 0) {
                    enemyBullets.add(new EnemyBullet(this.x, this.y, vx, vy));
                }
                for (int b = 0; b < enemyBullets.size(); b++) {
                    EnemyBullet enBull = enemyBullets.get(b);
                    enBull.move();
                    enBull.drawBullet();
                    if (enBull.x < 0 || enBull.x > width || enBull.y < 0 || enBull.y > height) {
                        enemyBullets.remove(b);
                    }
                    if(abs(playerX-enBull.x)<15 && abs(playerY-enBull.y)<15){
                        if (score > highScore) {
                            highScore = score;
                        }
                        currentState = GameState.OVER;
                        break;
                    }
                }
            }
        }

    }

    class Boss extends Enemy{
        Boss(float x, float y, int enemyType){
            super(x, y, enemyType);
        }

    }

    class Bullet {
        float x, y, vx, vy;

        Bullet(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void drawBullet() {
            imageMode(CENTER);
            image(bulletAnimation[bulletFrame], x, y);
        }

        void move() {
            x += vx;
            y += vy;
        }
    }
    class EnemyBullet extends Bullet{
        EnemyBullet(float x, float y, float vx, float vy) {
            super(x, y, vx*2, vy*2);
        }

        void drawBullet() {
            imageMode(CENTER);
            image(enemyBulletAnimations[animationFrame], x, y);

        }
    }
}