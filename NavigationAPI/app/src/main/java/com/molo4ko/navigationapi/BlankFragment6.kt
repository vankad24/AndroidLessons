package com.molo4ko.navigationapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class BlankFragment6 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment6, container, false)


        val backButton: Button = view.findViewById(R.id.ButtonBack)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}