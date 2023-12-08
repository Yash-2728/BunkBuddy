package com.example.bunkbuddy.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bunkbuddy.databinding.FragmentTimetableContentBinding
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.util.TimetableAdapter

class TimetableContentFragment(private val lectures: LiveData<List<Lecture>>) : Fragment() {

    private var _binding: FragmentTimetableContentBinding? = null
    private val binding get()=_binding!!
    private lateinit var adapter: TimetableAdapter
    private var textType = 0

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object: Runnable{
        override fun run() {
            adapter.changeText(textType)
            textType = 1 - textType
            handler.postDelayed(this, 2000)
        }
    }
    private fun startLoop(){
        handler.postDelayed(runnable, 2000)
    }
    private fun stopLoop(){
        handler.removeCallbacks(runnable)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        stopLoop()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimetableContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        lectures.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }

    private fun setUpRecyclerView() {
        adapter = TimetableAdapter(requireContext())
        binding.timetableContentRcv.adapter = adapter
        binding.timetableContentRcv.layoutManager = LinearLayoutManager(requireContext())
        startLoop()
    }
}