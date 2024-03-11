package com.tejasdev.bunkbuddy.fragments

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
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.databinding.FragmentTimetableContentBinding
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.util.adapters.TimetableAdapter
import com.google.android.material.snackbar.Snackbar
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.util.constants.LECTURE_DELETED
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimetableContentFragment() : Fragment() {

    private var _binding: FragmentTimetableContentBinding? = null
    private val binding get()=_binding!!
    private lateinit var adapter: TimetableAdapter
    private var textType = 0
    private lateinit var viewModel: SubjectViewModel
    private lateinit var lectures: LiveData<List<Lecture>>
    private var dayNumber: Int = 0
    private lateinit var alarmViewModel: AlarmViewModel

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
        arguments?.let {
            dayNumber = it.getInt("dayNumber")
        }
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
        alarmViewModel = (activity as MainActivity).alarmViewModel
        instantiateViewmodel()
        setUpRecyclerView()
        lectures.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
            binding.sizeTv.text = "Showing ${it.size} results"
        })
    }

    private fun instantiateViewmodel() {
        viewModel = (activity as MainActivity).viewModel
        lectures = when(dayNumber){
            0-> viewModel.monday
            1-> viewModel.tuesday
            2-> viewModel.wednesday
            3-> viewModel.thursday
            4-> viewModel.friday
            5-> viewModel.saturday
            else -> viewModel.sunday
        }
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
                    showUndoSnackbar(lecture, pos)

                }

            }
        )
        itemTouchHelper.attachToRecyclerView(binding.timetableContentRcv)
    }
    private fun showUndoSnackbar(deletedItem: Lecture, position: Int) {
        val snackbar = Snackbar.make(
            requireView(),
            "Lecture deleted",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("Undo") {
            undoDelete(deletedItem, position)
        }

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event != DISMISS_EVENT_ACTION) {
                    viewModel.deleteLecture(deletedItem)
                    alarmViewModel.cancelAlarm(deletedItem)
                    val dayAndDate = getDayAndDate()
                    val historyItem = HistoryItem(
                        LECTURE_DELETED,
                        String.format(
                            requireContext().getString(
                                R.string.added_lecture
                            ),
                            deletedItem.subject.name,
                            getDay(deletedItem.dayNumber),
                            deletedItem.startTime,
                            deletedItem.endTime
                        ),
                        time = dayAndDate[0],
                        date = dayAndDate[1]
                    )
                    viewModel.addHistory(historyItem)
                }
            }
        })

        snackbar.show()
    }
    private fun getDayAndDate():List<String> {
        val currentDate = Calendar.getInstance().time

        return listOf(
            SimpleDateFormat("hh:mm a", Locale.US).format(currentDate),
            SimpleDateFormat("d MMM yyyy", Locale.US).format(currentDate)
        )
    }
    private fun getDay(num: Int): String{
        return when(num){
            0-> requireContext().getString(R.string.monday)
            1-> requireContext().getString(R.string.tuesday)
            2-> requireContext().getString(R.string.wednesday)
            3-> requireContext().getString(R.string.thursday)
            4-> requireContext().getString(R.string.friday)
            5-> requireContext().getString(R.string.saturday)
            else -> requireContext().getString(R.string.sunday)
        }
    }
    private fun undoDelete(deletedItem: Lecture, position: Int) {
        adapter.addItem(deletedItem, position)
    }
}