package com.example.bunkbuddy.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.R
import com.example.bunkbuddy.UI.SubjectViewModel
import com.example.bunkbuddy.activities.MainActivity
import com.example.bunkbuddy.databinding.FragmentTimetableBinding
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.util.TimetableAdapter
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SubjectViewModel
    private var popupWindow: PopupWindow? = null
    private var textType = 0
    private lateinit var adapter: TimetableAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object: Runnable{
        override fun run() {
            adapter.changeText(textType)
            textType = 1 - textType
            handler.postDelayed(this, 2000)
        }
    }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.chipgroup.clearCheck()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val list = viewModel.getAllSubjectSync()
        val days = resources.getStringArray(R.array.days).toList()

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val chipIndex = (today+5)%7
        selectChip(chipIndex)
        setUpRecyclerView(chipIndex)
        startLoop()
        binding.mondayChip.setOnClickListener{
            viewModel.monday.observe(viewLifecycleOwner, Observer{
                adapter.setData(it)
            })
        }
        binding.tuesdayChip.setOnClickListener {
            viewModel.tuesday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.wednesdayChip.setOnClickListener {
            viewModel.wednesday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.thursdayChip.setOnClickListener {
            viewModel.thursday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.fridayChip.setOnClickListener {
            viewModel.friday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.saturdayChip.setOnClickListener {
            viewModel.saturday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.sundayChip.setOnClickListener {
            viewModel.sunday.observe(viewLifecycleOwner, Observer {
                adapter.setData(it)
            })
        }
        binding.addLectureBtn.root.setOnClickListener {
            showAddLecturePopup(list, days)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLoop()
    }

    private fun startLoop(){
        handler.postDelayed(runnable, 2000)
    }
    private fun stopLoop(){
        handler.removeCallbacks(runnable)
    }

    private fun setUpRecyclerView(initIndex: Int) {
        adapter = TimetableAdapter(requireContext())
        binding.rcv.adapter = adapter
        binding.rcv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val initViewModel = when(initIndex){
            0->viewModel.monday
            1->viewModel.tuesday
            2->viewModel.wednesday
            3->viewModel.thursday
            4->viewModel.friday
            5->viewModel.saturday
            else->viewModel.sunday
        }
        initViewModel.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

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
                    viewModel.deleteLecture(lecture)
                    adapter.remove(viewHolder.adapterPosition)
                }

            }
        )
        itemTouchHelper.attachToRecyclerView(binding.rcv)
    }

    private fun selectChip(index: Int){
        Log.w("bunkbuddyerrorlogs", "$index ${binding.chipgroup.childCount}")
        if(index<binding.chipgroup.childCount){
            val chip = binding.chipgroup.getChildAt(index) as Chip
            Log.w("bunkbuddyerrorlogs", "${chip.text}")
            chip.isChecked = true
            Handler().postDelayed({
                binding.scrollView.smoothScrollTo(chip.left, 0)
            }, 500)
        }
    }

    private fun showAddLecturePopup(subjects: List<Subject>, days: List<String>){
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.add_lecture_popup, null)
        val subjectDropdown: AutoCompleteTextView = popupView.findViewById(R.id.autoCompleteSubject)
        val cancelBtn: Button = popupView.findViewById(R.id.btn_cancel)
        val dayDropdown: AutoCompleteTextView = popupView.findViewById(R.id.autoCompleteDay)
        val addBtn: Button = popupView.findViewById(R.id.btn_add)
        val startTime: TextView = popupView.findViewById(R.id.start_time_tv)
        val endTime: TextView = popupView.findViewById(R.id.end_time_tv)
        val startTimeCard: CardView = popupView.findViewById(R.id.start_time_card)
        val endTimeCard: CardView = popupView.findViewById(R.id.end_time_card)
        val timeAndDate = getDayAndDate()
        startTime.text = timeAndDate[0]
        endTime.text = timeAndDate[0]
        val timepickerLayout = LayoutInflater.from(requireContext()).inflate(R.layout.time_spinner_dialog, null)
        val timepicker: TimePicker = timepickerLayout.findViewById(R.id.time_picker)
        timepicker.setIs24HourView(false)


        val startTimeLive: MutableLiveData<String> = MutableLiveData(timeAndDate[0])
        val endTimeLive: MutableLiveData<String> = MutableLiveData(timeAndDate[0])
        startTimeLive.observe(viewLifecycleOwner, Observer {
            startTime.text = it
        })
        endTimeLive.observe(viewLifecycleOwner, Observer {
            endTime.text = it
        })
        val timepickerWindow by lazy {
            PopupWindow(
                timepickerLayout,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        timepickerWindow.isFocusable=true

        var startTimePickerVisible = false
        var endTimePickerVisible = false
        startTime.setOnClickListener {
            if(endTimePickerVisible) {
                timepickerWindow.dismiss()
                endTimePickerVisible = false
            }
            startTimePickerVisible = true
            timepickerWindow.showAsDropDown(startTimeCard)
        }
        endTime.setOnClickListener {
            if(startTimePickerVisible) {
                timepickerWindow.dismiss()
                startTimePickerVisible = false
            }
            endTimePickerVisible = true
            timepickerWindow.showAsDropDown(endTimeCard)
        }
        timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val aMPM = if(hourOfDay>11) "PM" else "AM"
            var hour = hourOfDay
            if(hour>11) hour %= 12
            if(hour==0) hour=12
            var min = minute.toString()
            if(minute<10) min="0$min"
            if(endTimePickerVisible){
                endTimeLive.postValue("${hour}:${min} $aMPM")
            }
            else if(startTimePickerVisible){
                startTimeLive.postValue("${hour}:${min} $aMPM")
            }
            else {
                Toast.makeText(requireContext(), "states: $startTimePickerVisible $endTimePickerVisible", Toast.LENGTH_SHORT).show()
            }
        }


        addBtn.isEnabled = false
        addBtn.setTextColor(resources.getColor(R.color.text_primary))

        var subject: Subject? = null
        var day: Int? = null
        val subjectNames = subjects.map { it.name }

        val subjectAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, subjectNames)
        subjectDropdown.setAdapter(subjectAdapter)
        subjectDropdown.setOnItemClickListener { _, _, position, _ ->
            subject = subjects[position]
            addBtn.isEnabled = (day!=null && subject!=null)
            if(addBtn.isEnabled) addBtn.setTextColor(resources.getColor(R.color.primary_blue))
            else addBtn.setTextColor(resources.getColor(R.color.text_primary))
        }

        val daysAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, days)
        dayDropdown.setAdapter(daysAdapter)
        dayDropdown.setOnItemClickListener { _, _, position, _ ->
            day = position
            addBtn.isEnabled = (day!=null && subject!=null)
            if(addBtn.isEnabled) addBtn.setTextColor(resources.getColor(R.color.primary_blue))
            else addBtn.setTextColor(resources.getColor(R.color.text_primary))
        }

        addBtn.setOnClickListener {
            Log.w("bunkbuddyerrorlogs" , "$day $subject ${startTimeLive.value} ${endTimeLive.value}")
            addLecture(day!!, subject!!,startTimeLive.value!! ,endTimeLive.value!!)
            popupWindow?.dismiss()
        }
        cancelBtn.setOnClickListener {
            popupWindow?.dismiss()
        }

        popupWindow = PopupWindow(
            popupView,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        popupWindow?.isFocusable=true
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up_in_anim)
        popupView.startAnimation(animation)
        popupWindow?.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)
    }

    private fun getDayAndDate():List<String> {
        val currentDate = Calendar.getInstance().time

        return listOf(
            SimpleDateFormat("hh:mm a", Locale.US).format(currentDate),
            SimpleDateFormat("d MMM yyyy", Locale.US).format(currentDate)
        )
    }

    private fun addLecture(
        dayNumber:Int,
        subject: Subject,
        start: String,
        end: String){

        val lecture = Lecture(dayNumber, subject, start, end, 0, subject.id)
        viewModel.addLecture(lecture)

    }

}