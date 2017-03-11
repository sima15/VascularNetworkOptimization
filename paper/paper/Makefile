TARGETS := cibcb2017.pdf

all: $(TARGETS)

view: $(TARGETS)
	evince $(TARGETS)

clean:
	-rm -f *.aux *.log *.dvi *~
	-rm -f *.bbl *.blg

spotless:
	make clean
#	-rm -f *.ps *.pdf
	@echo removing '*.pdf' is dangerous, what if we include prebuilts?

cibcb2017.pdf: cibcb2017.tex method_abstract.tex method_CONCLUSION.tex method_INTRODUCTION.tex method_METHOD.tex method_RESULTS.tex Model_Description.tex nicholasflann.bib qanitabaker-fittingthree.bib qanitabaker-paperone.bib qanitabaker-proposal.bib qanitabaker-retinal.bib

%.pdf: %.tex
	pdflatex $*.tex
	bibtex $*.aux
	pdflatex $*.tex
	pdflatex $*.tex

