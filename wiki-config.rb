# A small prerequisite to the next line. Don't pay attention to this.
Gollum::Page.send :remove_const, :FORMAT_NAMES if defined? Gollum::Page::FORMAT_NAMES

# Specify formats used by the wiki. Formats not specified will not be displayed/created/used.
# Use the list of formats bellow.
Gollum::Page::FORMAT_NAMES = { :asciidoc => "AsciiDoc", :markdown => "Markdown", :mediawiki => "MediaWiki", :textile => "Textile" }

=begin
Format definitions supported by the wiki:
{
    :markdown => "Markdown",
    :textile => "Textile",
    :rdoc => "RDoc",
    :org => "Org-mode",
    :creole => "Creole",
    :rest => "reStructuredText",
    :asciidoc => "AsciiDoc",
    :mediawiki => "MediaWiki",
    :pod => "Pod"
}
=end