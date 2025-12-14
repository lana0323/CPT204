#define FREEGLUT_STATIC
#include <math.h>
#include <GL/freeglut.h>
#include <cmath>
#include <iostream>

//set pi value
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

void init();
void timer(int);

void onMouseClick(int, int, int, int);
void onMouseDrag(int, int);
void onKeyboardPress(unsigned char, int, int);

void background();

float getStrokeTextWidth(const char*);
void drawStrokeText(const char*, float, float, float);

void drawSmallHeart(float, float, float);
void drawCake();
void drawCandle(float, float, float, float, float, float);

void drawEllipseBalloon(float, float, float, float);

void display();


// text initial settings
float textScale = 0.3f;  // initial scaling
bool textIncreasing = true;  // the direction of scaling

// flame initial settings
float flameHeight = 15.0f; // initial height
bool flameIncreasing = true; // the direction

// initial settings of balloons
const int balloonCount = 14;
float balloonX[balloonCount] = { -275.0f, 200.0f, -200.0f, 350.0f, -350.0f, 250.0f, -250.0f, 325.0f, -300.0f, 225.0f, 350.0f, -225.0f, 250.0f, -350.0f };
float balloonY[balloonCount] = { -300, -350, -400, -450, -500, -550, -600, -650, -700, -750, -800, -850, -900, -950};
bool balloonVisible[balloonCount] = { true, true, true, true, true, true, true, true, true, true, true, true, true, true };

float balloonSpeed = 3.0f;  // initial speed
float minSpeed = 1.0f;
float maxSpeed = 10.0f;

// mouse detection
bool isDragging = false;
int draggedBalloon = -1;

// keyboeard pausing detection
bool isPaused = false;

// initial settings of the screen
void init() {
    glClearColor(1.0, 1.0, 1.0, 1.0);
    glClear(GL_COLOR_BUFFER_BIT);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluOrtho2D(-400, 400, -250, 250);
}

// the timer of the project for animations
void timer(int value) {
    if (!isPaused) {
        // text animation
        if (textIncreasing) {
            textScale += 0.005f;
            if (textScale >= 0.4f) {
                textIncreasing = false;
            }
        }
        else {
            textScale -= 0.005f;
            if (textScale <= 0.3f) {
                textIncreasing = true;
            }
        }

        // flame animation
        if (flameIncreasing) {
            flameHeight += 0.9f;
            if (flameHeight >= 20.0f) {
                flameIncreasing = false;
            }
        }
        else {
            flameHeight -= 0.9f;
            if (flameHeight <= 15.0f) { 
                flameIncreasing = true;
            }
        }

        // balloons animation
        for (int i = 0; i < balloonCount; ++i) {
            if (i != draggedBalloon) {
                balloonY[i] += balloonSpeed;
                if (balloonY[i] > 400) {
                    balloonY[i] = -300;
                    balloonVisible[i] = true;
                }
            }
        }
    }

    glutPostRedisplay();
    glutTimerFunc(40, timer, 0);
}

// mouse control settings
void onMouseClick(int button, int state, int x, int y) {
    float mouseX = x - 400;
    float mouseY = 250 - y;

    // left button
    if (button == GLUT_LEFT_BUTTON) {
        if (state == GLUT_DOWN) {
            for (int i = 0; i < balloonCount; ++i) {
                if (mouseX >= balloonX[i] - 20.0f && mouseX <= balloonX[i] + 20.0f &&
                    mouseY >= balloonY[i] - 30.0f && mouseY <= balloonY[i] + 30.0f) {
                    isDragging = true;
                    draggedBalloon = i;
                    break;
                }
            }
        }
        else if (state == GLUT_UP) {
            isDragging = false;
            draggedBalloon = -1;
        }
    }

    // right button
    if (button == GLUT_RIGHT_BUTTON && state == GLUT_DOWN) {
        for (int i = 0; i < balloonCount; ++i) {
            if (mouseX >= balloonX[i] -20.0f && mouseX <= balloonX[i] + 20.0f &&
                mouseY >= balloonY[i] - 30.0f && mouseY <= balloonY[i] + 30.0f) {
                balloonVisible[i] = false;
            }
        }
    }
}

// mouse dragging to update the position of balloons
void onMouseDrag(int x, int y) {
    if (isDragging && draggedBalloon != -1) {
        float mouseX = x - 400;
        float mouseY = 250 - y;

        balloonY[draggedBalloon] = mouseY;
        balloonX[draggedBalloon] = mouseX;
    }
}

// keyboard control settings
void onKeyboardPress(unsigned char key, int x, int y) {
    if (key == 'p' || key == 'P') {
        // press 'p' to stop animations
        isPaused = true;
    }
    else if (key == 's' || key == 'S') {
        // press 's' to start animations
        isPaused = false;
    }
    else if (key == 'r' || key == 'R') {
        // press 'r' to reset the animations
        // reposition all the balloons
        float resetBalloonX[balloonCount] = { -275.0f, 200.0f, -200.0f, 350.0f, -350.0f, 250.0f, -250.0f, 325.0f, -300.0f, 225.0f, 350.0f, -225.0f, 250.0f, -350.0f };
        for (int i = 0; i < balloonCount; ++i) {
            balloonX[i] = resetBalloonX[i];
            balloonY[i] = -300 - (i * 50);
        }
        // start the animations
        isPaused = false;
        // reset the speed
        balloonSpeed = 3.0f;
    }
    else if (key == 'a' || key == 'A') {
        // press 'a' to speed up balloons
        if (balloonSpeed < maxSpeed) {
            balloonSpeed += 0.5f;
        }
    }
    else if (key == 'd' || key == 'D') {
        // press 'd' to slow down balloons
        if (balloonSpeed > minSpeed) {
            balloonSpeed -= 0.5f;
        }
    }
}

// background settings
void background()
{
    glBegin(GL_QUADS);

    // top is purple
    glColor3f(0.9f, 0.8f, 1.0f);
    glVertex2f(-400, 250);
    glVertex2f(400, 250);

    // base is blue
    glColor3f(0.8f, 0.9f, 1.0f);
    glVertex2f(400, -250);
    glVertex2f(-400, -250);

    glEnd();
}

// calculate the width of the text
float getStrokeTextWidth(const char* text) {
    float width = 0.0f;
    for (const char* c = text; *c != '\0'; ++c) {
        width += glutStrokeWidth(GLUT_STROKE_ROMAN, *c);
    }
    return width;
}

// draw the text
void drawStrokeText(const char* text, float x, float y, float scale) {
    glColor3f(1.0, 0.0, 0.0);
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(scale, scale, scale);

    for (const char* c = text; *c != '\0'; ++c) {
        glutStrokeCharacter(GLUT_STROKE_ROMAN, *c); 
    }

    glPopMatrix();
}

// draw heart
void drawSmallHeart(float x, float y, float scale) {
    glColor3f(1.0, 0.0, 0.0);
    glBegin(GL_POLYGON);
    for (int i = 180; i <= 360 + 180; ++i) {
        double angle = i * M_PI / 180.0;
        float heartX = x + scale * 16 * pow(sin(angle), 3);
        float heartY = y + scale * (13 * cos(angle) - 5 * cos(2 * angle) - 2 * cos(3 * angle) - cos(4 * angle));
        glVertex2f(heartX, heartY);
    }
    glEnd();
}


// draw cake
void drawCake() {
    // colors of each cylinder
    float colors[3][3] = {
        {1.0f, 0.8f, 0.6f}, 
        {1.0f, 0.85f, 0.7f},
        {1.0f, 0.9f, 0.8f}
    };

    // colors of each upper ellipse
    float topColors[3][3] = {
        {1.0f, 0.9f, 0.7f},
        {1.0f, 0.9f, 0.75f},
        {1.0f, 0.95f, 0.85f}
    };

    // parameters of buttom cylinder
    float bottomWidth = 300.0f;
    float height = 70.0f;
    float x1 = -bottomWidth / 2;
    float y1 = -150.0f;
    
    // draw rectangle
    glColor3fv(colors[0]);
    glBegin(GL_QUADS);
    glVertex2f(x1, y1);
    glVertex2f(x1, y1 + height);
    glVertex2f(x1 + bottomWidth, y1 + height);
    glVertex2f(x1 + bottomWidth, y1);
    glEnd();

    float bottomArcHeightRatio = 1.9f;

    // draw lower ellipse
    glColor3fv(colors[0]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x1 + bottomWidth / 2, y1);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX1 = x1 + bottomWidth / 2 + (bottomWidth / 2) * cos(angle);
        float arcY1 = y1 + (height / bottomArcHeightRatio) * sin(angle);
        glVertex2f(arcX1, arcY1);
    }
    glEnd();

    // draw upper ellipse
    glColor3fv(topColors[0]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x1 + bottomWidth / 2, y1 + height);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX1 = x1 + bottomWidth / 2 + (bottomWidth / 2) * cos(angle);
        float arcY1 = y1 + height + (height / bottomArcHeightRatio) * sin(angle);
        glVertex2f(arcX1, arcY1);
    }
    glEnd();

    // middle cylinder parameters
    float currentWidth = bottomWidth - 80.0f;
    float height2 = 60.0f;
    float x2 = x1 + 40.0f;
    float y2 = y1 + height;

    //rectangle
    glColor3fv(colors[1]);
    glBegin(GL_QUADS);
    glVertex2f(x2, y2);
    glVertex2f(x2, y2 + height2);
    glVertex2f(x2 + currentWidth, y2 + height2);
    glVertex2f(x2 + currentWidth, y2);
    glEnd();

    // lower ellipse
    glColor3fv(colors[1]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x2 + currentWidth / 2, y2);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX2 = x2 + currentWidth / 2 + (currentWidth / 2) * cos(angle);
        float arcY2 = y2 + (height2 / 4) * sin(angle);
        glVertex2f(arcX2, arcY2);
    }
    glEnd();

    // upper ellipse
    glColor3fv(topColors[1]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x2 + currentWidth / 2, y2 + height2);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX2 = x2 + currentWidth / 2 + (currentWidth / 2) * cos(angle);
        float arcY2 = y2 + height2 + (height2 / 4) * sin(angle);
        glVertex2f(arcX2, arcY2);
    }
    glEnd();

    // top cylinder parameters
    float currentWidth2 = currentWidth - 80.0f;
    float height3 = 50.0f;
    float x3 = x2 + 40.0f;
    float y3 = y2 + height2;

    // draw rectangle
    glColor3fv(colors[2]);
    glBegin(GL_QUADS);
    glVertex2f(x3, y3);
    glVertex2f(x3, y3 + height3);
    glVertex2f(x3 + currentWidth2, y3 + height3);
    glVertex2f(x3 + currentWidth2, y3);
    glEnd();

    float bottomArcHeightRatio3 = 6.9f;

    // draw lower ellipse
    glColor3fv(colors[2]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x3 + currentWidth2 / 2, y3);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX3 = x3 + currentWidth2 / 2 + (currentWidth2 / 2) * cos(angle);
        float arcY3 = y3 + (height3 / bottomArcHeightRatio3) * sin(angle);
        glVertex2f(arcX3, arcY3);
    }
    glEnd();

    // draw upper ellipse
    glColor3fv(topColors[2]);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x3 + currentWidth2 / 2, y3 + height3);
    for (int j = 0; j <= 360; j += 5) {
        float angle = j * M_PI / 180.0f;
        float arcX3 = x3 + currentWidth2 / 2 + (currentWidth2 / 2) * cos(angle);
        float arcY3 = y3 + height3 + (height3 / bottomArcHeightRatio3) * sin(angle);
        glVertex2f(arcX3, arcY3);
    }
    glEnd();

    // position of candle
    float candleX1 = x3 + currentWidth2 / 2;
    float candleY1 = y3 + height3;
    

    // draw candle
    drawCandle(candleX1, candleY1, 10.0f, 40.0f, 8.0f, flameHeight);

    // top hearts
    float topHeartX = x3 + 70.0f;
    float topHeartY = y3 + height3;

    // three hearts
    drawSmallHeart(topHeartX - 50.0f, topHeartY - 25.0f, 0.6f);
    drawSmallHeart(topHeartX, topHeartY - 30.0f, 0.6f);
    drawSmallHeart(topHeartX + 50.0f, topHeartY - 25.0f, 0.6f);

    // middle hearts
    float midHeartX = x2 + 110.0f;
    float midHeartY = y2 + height2;

    // four hearts
    drawSmallHeart(midHeartX - 30.0f, midHeartY - 45.0f, 0.8f);
    drawSmallHeart(midHeartX + 30.0f, midHeartY - 45.0f, 0.8f);
    drawSmallHeart(midHeartX - 80.0f, midHeartY - 35.0f, 0.8f);
    drawSmallHeart(midHeartX + 80.0f, midHeartY - 35.0f, 0.8f);

    // base hearts
    float bottomHeartX = x1 + 150.0f;
    float bottomHeartY = y1 + height;
    
    // five hearts
    drawSmallHeart(bottomHeartX, bottomHeartY - 75.0f, 1.0f);
    drawSmallHeart(bottomHeartX - 60.0f, bottomHeartY - 67.5f, 1.0f);
    drawSmallHeart(bottomHeartX + 60.0f, bottomHeartY - 67.5f, 1.0f);
    drawSmallHeart(bottomHeartX - 120.0f, bottomHeartY - 55.0f, 1.0f);
    drawSmallHeart(bottomHeartX + 120.0f, bottomHeartY - 55.0f, 1.0f);

}

// draw candle
void drawCandle(float x, float y, float candleWidth, float candleHeight, float flameWidth, float flameHeight) {
    // flame
    glColor3f(1.0f, 0.0f, 0.0f);
    glBegin(GL_POLYGON);
    for (int i = 0; i <= 360; i += 10) {
        float angle = i * M_PI / 180.0f;
        float fx = x + flameWidth * cos(angle) / 2;
        float fy = y + candleHeight + flameHeight * sin(angle);
        glVertex2f(fx, fy);
    }
    glEnd();
    
    // candle shaft
    glColor3f(1.0f, 0.6f, 0.6f);
    glBegin(GL_QUADS);
    glVertex2f(x - candleWidth / 2, y);
    glVertex2f(x + candleWidth / 2, y);
    glVertex2f(x + candleWidth / 2, y + candleHeight);
    glVertex2f(x - candleWidth / 2, y + candleHeight);
    glEnd();
}

// draw balloons
void drawEllipseBalloon(float x, float y, float radiusX, float radiusY) {
    // balloon body
    glColor3f(1.0f, 0.6f, 0.8f);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(x, y);
    for (int i = 0; i <= 360; i += 10) {
        float angle = i * M_PI / 180.0f;
        float bx = x + radiusX * cos(angle);
        float by = y + radiusY * sin(angle);
        glVertex2f(bx, by);
    }
    glEnd();

    // balloon string
    float yStart = y - 30.0f;
    float yEnd = y - 100.0f;

    glColor3f(0.6f, 0.4f, 0.2f);
    glBegin(GL_LINE_STRIP);

    // control point
    float control1X = x +  radiusX;
    float control1Y = yStart - 30.0f;
    float control2X = x -  radiusX * 0.7f;
    float control2Y = yStart - 30.0f;

    
    for (float t = 0; t <= 1.0f; t += 0.01f) {
        float xt = pow(1 - t, 3) * x +
            3 * pow(1 - t, 2) * t * control1X +
            3 * (1 - t) * pow(t, 2) * control2X +
            pow(t, 3) * x;

        float yt = pow(1 - t, 3) * yStart +
            3 * pow(1 - t, 2) * t * control1Y +
            3 * (1 - t) * pow(t, 2) * control2Y +
            pow(t, 3) * yEnd;

        glVertex2f(xt, yt);
    }

    glEnd();
}

void display() {
    glClear(GL_COLOR_BUFFER_BIT);

    background();

    drawCake();

    for (int i = 0; i < balloonCount; ++i) {
        if (balloonVisible[i]) {
            drawEllipseBalloon(balloonX[i], balloonY[i], 20.0f, 30.0f);
        }

    }

    const char* text = "Happy Birthday!";
    float textWidth = getStrokeTextWidth(text) * textScale;
    float x = -textWidth / 2.0f;
    float y = 150.0f;
    
    drawStrokeText(text, x, y, textScale);
    glFlush();
}



int main(int argc, char** argv)
{
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
    glutInitWindowPosition(800, 450);
    glutInitWindowSize(800, 500);
    glutCreateWindow("CW1");

    init();
    glutDisplayFunc(display);

    glutTimerFunc(30, timer, 0);

    // mouse and keyboard registration
    glutMouseFunc(onMouseClick);
    glutMotionFunc(onMouseDrag);
    glutKeyboardFunc(onKeyboardPress);

    glutMainLoop();
    return 0;
}

