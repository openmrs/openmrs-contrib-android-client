package com.example.diceroller

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var mDice: Button
    lateinit var mDiceImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        mDice.setOnClickListener {
            var randomInteger : Int = Random.nextInt(6) + 1
            var diceImageResource = when(randomInteger){
                1-> R.drawable.dice_1
                2->R.drawable.dice_2
                3->R.drawable.dice_3
                4->R.drawable.dice_4
                5->R.drawable.dice_5
                6->R.drawable.dice_6
                else->R.drawable.empty_dice
            }
            mDiceImage.setImageResource(diceImageResource)
        }
    }

    private fun initializeViews() {
        mDice = findViewById(R.id.roll_button)
        mDiceImage = findViewById(R.id.dice_image)
    }
}
