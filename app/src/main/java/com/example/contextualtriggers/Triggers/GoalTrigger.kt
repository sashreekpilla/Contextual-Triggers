package com.example.contextualtriggers.Triggers

import android.util.Log
import org.json.JSONObject

object GoalTrigger {
    fun goalTrigger(currentSteps: Int, targetSteps: Int): String {
        var triggerText = ""
        if(targetSteps *0.5  == currentSteps.toDouble()){
            triggerText = "You are Halfway there!!!!"
        } else if(targetSteps*0.7 == currentSteps.toDouble()){
            triggerText = "You are close to complete your goal"
        } else if(targetSteps*0.9 == currentSteps.toDouble()){
            triggerText = "You are almost there"
        }else if(targetSteps == currentSteps){
            triggerText = "Congratulations you have completed your goal"
        }
        return triggerText
    }

}