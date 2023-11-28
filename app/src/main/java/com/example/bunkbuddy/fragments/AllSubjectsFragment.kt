package com.example.bunkbuddy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bunkbuddy.R
import com.example.bunkbuddy.databinding.FragmentAllSubjectsBinding

class AllSubjectsFragment : Fragment() {
    private var _binding: FragmentAllSubjectsBinding? = null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }
}