codepath-image-search
=======================

Google Image Search Project

An app that displays google image search results based on user input query as well as filter settings

Time spent: ~10 hours spent in total

Completed user stories:

 * [x] User can enter a search query that will display a grid of image results from the Google Image API.
 * [x] User can click on "settings" which allows selection of advanced search options to filter results
 * [x] User can configure advanced search filters such as:
          Size (small, medium, large, extra-large)
          Color filter (black, blue, brown, gray, green, etc...)
          Type (faces, photo, clip art, line art)
          Site (espn.com)
 * [x] Subsequent searches will have any filters applied to the search results
 * [x] User can tap on any image in results to see the image full-screen
 * [x] User can scroll down “infinitely” to continue loading more image results (up to 8 pages)
 * [x] The following advanced user stories are optional:

 * [x] Advanced: Robust error handling, check if internet is available, handle error cases, network failures
 * [x] Advanced: Use the ActionBar SearchView or custom layout as the query box instead of an EditText
 * [] Advanced: User can share an image to their friends or email it to themselves
 * [x] Advanced: Replace Filter Settings Activity with a lightweight modal overlay
 * [x] Advanced: Improve the user interface and experiment with image assets and/or styling and coloring
 * [] Bonus: Use the StaggeredGridView to display visually interesting image results
 * [] Bonus: User can zoom or pan images displayed in full-screen detail view

Notes:
* Wanted to get the images to auto resize and fill the grid view instead of leaving white spaces but couldn't get it to work. Would love to hear suggestions.
* Controls (Spinners and EditText are sticking out of the modal dialog and I haven't been able to fix it yet)


![Video Walkthrough](GoogleImageSearch.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).
