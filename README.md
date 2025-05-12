# OpenProject Wiki Exporter

This is a quick simple program to export OpenProject wiki pages to markdown files.
It parses the main page to list all wiki pages and then fetches each page to save it as a markdown file.

Bulk export is currently not available in OpenProject and the API does not really support Wiki Pages.

Tested with OpenProject 15.5.0

## Requirements

- Java (I am using 21, didn't check with other versions)

## Usage

1. Clone the repository
2. create the file `cookie`
3. Open the OpenProject wiki in your browser and log in
4. Open the developer tools
5. Go to the "Network" tab
6. Refresh the page
7. Find the request to the wiki page (it might be the first one)
8. Find the `Cookie` header in the request and copy it
9. Paste it in the `cookie` file
10. Make sure the file is in the format `_open_project_session==some_mumbers`
11. Run the program with `java Main.java <basePath> <projectName>`
