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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.UI.SubjectViewModel
import com.example.bunkbuddy.databinding.FragmentTimetableContentBinding
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.util.TimetableAdapter
import com.google.android.material.snackbar.Snackbar

class TimetableContentFragment(private val lectures: LiveData<List<Lecture>>, private val viewModel: SubjectViewModel) : Fragment() {

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
        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    source: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePos = source.adapterPosition
                    val desPos = target.adapterPosition
                    adapter.swap(sourcePos, desPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val lecture = adapter.getAtPos(pos)
                    adapter.remove(pos)
                    viewModel.deleteLecture(lecture)
                    showUndoSnackbar(lecture, pos)

                }

            }
        )
        itemTouchHelper.attachToRecyclerView(binding.timetableContentRcv)
    }
    private fun showUndoSnackbar(deletedItem: Lecture, position: Int) {
        val snackbar = Snackbar.make(
            requireView(),
            "Subject deleted",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("Undo") {
            undoDelete(deletedItem, position)
        }

        snackbar.show()
    }
    private fun undoDelete(deletedItem: Lecture, position: Int) {
        viewModel.addLecture(deletedItem)
        adapter.addItem(deletedItem, position)
    }
}