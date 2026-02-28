package dsa.arrays;

/*

Asteroid Collision
Question
Given an array of integers representing asteroids in a row. For each asteroid, the absolute value represents size and sign represents direction:

Positive → moving right
Negative → moving left

Asteroids moving in the same direction never meet. When two asteroids collide, the smaller one explodes. If equal size, both explode. Find the state of asteroids after all collisions.
Sample Input 1:
        [5, 10, -5]
Sample Output 1:
        [5, 10]
        (-5 collides with 10, 10 wins)
Sample Input 2:
        [8, -8]
Sample Output 2:
        []
        (Both explode — equal size)
Sample Input 3:
        [10, 2, -5]
Sample Output 3:
        [10]
        (-5 destroys 2, then -5 collides with 10, 10 wins)
Sample Input 4:
        [-2, -1, 1, 2]
Sample Output 4:
        [-2, -1, 1, 2]
        (No collision — left-moving never meet right-moving here)

*/

/*

Key observations:
        1. Collision only happens when:
        → left asteroid is POSITIVE (moving right)
   → right asteroid is NEGATIVE (moving left)
   → they are moving TOWARD each other

2. No collision when:
        → both moving right  (+, +)
   → both moving left   (-, -)
   → left moving left, right moving right (-, +) → moving apart

3. Stack is perfect here:
        → process left to right
   → stack top = last surviving asteroid moving right
   → when we see negative asteroid, check stack top for collision
*/

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public class AsteroidCollision {

    public int [] asteroidCollision(int [] asteroids){
        Stack<Integer> stack = new Stack<>();
        for (int asteroid : asteroids){
            boolean destroyed = false;
            // Collision only when:
            // current asteroid moves LEFT (negative)
            // AND top of stack moves RIGHT (positive)
            while(!stack.empty() && stack.peek() > 0 && asteroid < 0){
                int left = stack.peek();
                int right = Math.abs(asteroid);

                if(left == right){
                    destroyed = true;
                    stack.pop();
                    break;
                }else if(left < right){
                    stack.pop();
                }else{
                    destroyed = true;
                    break;
                }

            }
            if(!destroyed)
                stack.push(asteroid);
        }

        int[] res = new int[stack.size()];
        for(int i = stack.size()-1; i>=0 ; i--){
            res[i] = stack.pop();
        }

        return  res;
    }

    public static void main(String[] args) {
        AsteroidCollision sol = new AsteroidCollision();

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{5, 10, -5})));
        // [5, 10]

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{8, -8})));
        // []

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{10, 2, -5})));
        // [10]

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{-2, -1, 1, 2})));
        // [-2, -1, 1, 2]

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{1, -1, 1, -1})));
        // []

        System.out.println(Arrays.toString(
                sol.asteroidCollision(new int[]{-2, -2, 1, -2})));
        // [-2, -2, -2]
    }
}

/*

Complexity

Time: O(n) — each asteroid pushed and popped at most once
Space: O(n) — stack holds at most n asteroids


What the Interviewer Expects
This is a classic stack simulation problem. The key is recognizing the collision condition precisely.
They check:

Do you identify stack as the right data structure
The exact collision condition: asteroid < 0 && stack.peek() > 0
Three collision outcomes handled correctly: top smaller, equal, top larger
Why it's O(n) despite the while loop — amortized, each asteroid pushed/popped once

*/
