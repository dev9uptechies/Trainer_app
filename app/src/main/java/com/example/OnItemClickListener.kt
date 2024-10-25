package com.example

import android.view.View

class OnItemClickListener(
    private val position: Int,
    private val onItemClickCallback: OnItemClickCallback,
    private val id: Long,
    private val string: String,
) : View.OnClickListener {

    override fun onClick(view: View) {
        onItemClickCallback.onItemClicked(view, position, id, string)
    }

    interface OnItemClickCallback {

        fun onItemClicked(
            view: View,
            position: Int,
            type: Long,
            string: String,
        )
    }

    interface WorkloadDialogListener {
        fun onWorkloadProgressSelected(progress: Int, colorCode: String)
    }

    interface AbilityNameCallback {
        fun onAbilityNameFetched(abilityId: Int, abilityName: String)
    }


}