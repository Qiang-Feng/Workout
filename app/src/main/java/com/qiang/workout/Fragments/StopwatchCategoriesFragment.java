package com.qiang.workout.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.qiang.workout.GraphActivity;
import com.qiang.workout.Models.Category;
import com.qiang.workout.R;
import com.qiang.workout.StopwatchTimesActivity;
import com.qiang.workout.Utilities.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopwatchCategoriesFragment extends Fragment
{
	private ListView categoriesView;
	private List<String> categoryStrings;
	private Map<Integer, Integer> categoryIDMap;

	private DBHandler dbHandler;

	private static final int CATEGORY_ADD = 1;
	private static final int CATEGORY_RENAME = 2;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflates the view
		View view = inflater.inflate(R.layout.fragment_stopwatch_categories, container, false);

		// Initialise variables
		categoriesView = (ListView) view.findViewById(R.id.categories_list);
		dbHandler = new DBHandler(getActivity(), null, null, 1);
		categoryIDMap = new HashMap<>();

		loadCategoriesList();

		// Allows for context menu when selecting a category
		registerForContextMenu(categoriesView);

		// Handles add category button click
		view.findViewById(R.id.button_add_category).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showEditTextDialog(CATEGORY_ADD);
			}
		});

		return view;
	}

	private void loadCategoriesList()
	{
		/*
	        Puts all category names in a list: categoryStrings
            Also updates the categoryIDMap
        */
		List<Category> categoryList = dbHandler.allCategories();
		categoryStrings = new ArrayList<>();

		for (int i = 0; i < categoryList.size(); i++)
		{
			categoryStrings.add(i, categoryList.get(i).getName());
			categoryIDMap.put(i, categoryList.get(i).getID());
		}

		// Displays the categoryStrings array in the categories list
		categoriesView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_item, categoryStrings));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
	{
		// Create management menu items if user selected from category list
		if (view.getId() == R.id.categories_list)
		{
			menu.add(getResources().getString(R.string.view_times));
			menu.add(getResources().getString(R.string.view_graph));
			menu.add(getResources().getString(R.string.rename));
			menu.add(getResources().getString(R.string.delete));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getTitle().toString().equals(getResources().getString(R.string.delete)))
		{
			// Deletes category and updates the category list
			dbHandler.deleteCategory(categoryIDMap.get(info.position));
			loadCategoriesList();
		}
		else if (item.getTitle().toString().equals(getResources().getString(R.string.rename)))
		{
			// Create rename popup
			showEditTextDialog(CATEGORY_RENAME, categoryIDMap.get(info.position));
		}
		else if (item.getTitle().toString().equals(getResources().getString(R.string.view_times)))
		{
			// Direct user to view times activity
			Intent intent = new Intent(getActivity(), StopwatchTimesActivity.class);
			intent.putExtra("stopwatchCategoryID", categoryIDMap.get(info.position));
			startActivity(intent);
		}
		else if (item.getTitle().toString().equals(getResources().getString(R.string.view_graph)))
		{
			// Direct user to view graph activity
			Intent intent = new Intent(getActivity(), GraphActivity.class);
			intent.putExtra("stopwatchCategoryID", categoryIDMap.get(info.position));
			startActivity(intent);
		}

		return super.onContextItemSelected(item);
	}

	private void showEditTextDialog(final int type)
	{
		showEditTextDialog(type, 0);
	}

	private void showEditTextDialog(final int type, final int categoryID)
	{
		// Initialise builder variable as a new AlertDialog object
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Sets a text input as the view/display for the dialog
		builder.setView(R.layout.dialog_edit_text);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// User clicked OK button
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// User clicked cancel button
			}
		});

		// Creates the dialog
		final AlertDialog dialog = builder.create();
		dialog.show();

		// Override click listeners to prevent dismissal of dialog if there are errors
		Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Get the text input object from the dialog
				EditText name = (EditText) dialog.findViewById(R.id.dialog_edit_text_input);

				/*
					Perform validation of input name
				*/
				boolean isError = false;

				if (name.getText().length() == 0)
				{
					isError = true;
					name.setError("Name is required");
				}

				if (name.getText().toString().trim().length() > 50)
				{
					isError = true;
					name.setError("Name can't be longer than 50 characters");
				}

				if (dbHandler.categoryExists(name.getText().toString().trim()))
				{
					if (type == CATEGORY_ADD)
					{
						isError = true;
						name.setError("Name already exists");
					}
				}

				/*
	                Produces an error if:
	                - name of category is taken and adding a new category
	                - changed category name is taken
	            */
				if (dbHandler.categoryExists(name.getText().toString().trim()))
				{
					if (type == CATEGORY_ADD || !name.getText().toString().trim().equals(dbHandler.getCategory(categoryID).getName()))
					{
						isError = true;
						name.setError("Name already exists");
					}
				}

				if (!isError)
				{
					// Determine whether to add or rename the category
					if (type == CATEGORY_ADD)
					{
						// Add the new category to the database
						dbHandler.addCategory(new Category(name.getText().toString()));
					}
					else if (type == CATEGORY_RENAME)
					{
						// Rename the selected category
						Category category = dbHandler.getCategory(categoryID);
						category.setName(name.getText().toString());

						dbHandler.saveEditedCategory(category);
					}

					// Refresh categories list
					loadCategoriesList();

					// Dismiss the dialog
					dialog.dismiss();
				}
			}
		});
	}
}
